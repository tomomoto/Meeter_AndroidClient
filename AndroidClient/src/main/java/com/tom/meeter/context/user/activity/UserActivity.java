package com.tom.meeter.context.user.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.event.activity.EventActivity.dispatchToEventActivity;
import static com.tom.meeter.infrastructure.common.CommonHelper.genderResolver;
import static com.tom.meeter.infrastructure.common.DateHelper.getAgeFromDate;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.showMessage;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.context.user.viewmodel.UserViewModel;
import com.tom.meeter.databinding.ActivityUserProfileBinding;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.components.adapter.EventsCardAdapter;
import com.tom.meeter.infrastructure.components.binder.PhotoDownloaderEventBinder;
import com.tom.meeter.infrastructure.http.ErrorLogger;
import com.tom.meeter.infrastructure.http.HttpCodes;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = UserActivity.class.getCanonicalName();
    public static final String USER_ID_KEY = "user_id";

    ActivityUserProfileBinding binding;
    @Inject
    TokenService tokenService;
    @Inject
    UserService userService;
    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    ImageDownloader imgDownloader;
    private UserViewModel userViewModel;
    private String userId;
    private AccountManager accountManager;
    private EventsCardAdapter adapter;
    private Boolean amISubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d(TAG, "Unable to create user activity without extras.");
            finish();
            return;
        }
        userId = extras.getString(USER_ID_KEY);
        if (userId == null) {
            Log.d(TAG, "Unable to create user activity without 'user_id' provided.");
            finish();
            return;
        }

        ((App) getApplication()).getUserComponent().inject(this);
        accountManager = AccountManager.get(this);

        adapter = new EventsCardAdapter(
              new PhotoDownloaderEventBinder(
                    this, imgDownloader, event -> dispatchToEventActivity(this, event.getId()),
                    this::recreate));

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken(this::onInit, this::finish, accountManager, this, tokenService);
    }

    private void onInit(String token) {
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.subscribeButton.setOnClickListener(v -> {
            if (amISubscriber == null) {
                // As not initialized atm...
                return;
            }
            if (amISubscriber) {
                userService.unsubscribe(Globals.getAuthHeader(token), userId).enqueue(new ErrorLogger<>(this) {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == HttpCodes.OK) {
                            amISubscriber = false;
                            updateSubscribeButtonText();
                            showMessage(UserActivity.this, "Successfully unsubscribed.");
                            return;
                        }
                        if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                            UserActivity.this.recreate();
                        }
                        Log.i(TAG, "/user/{id}/unsubscribe: " + response.code() + " : " + response.body());
                    }
                });
            } else {
                userService.subscribe(Globals.getAuthHeader(token), userId).enqueue(new ErrorLogger<>(this) {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == HttpCodes.OK) {
                            amISubscriber = true;
                            updateSubscribeButtonText();
                            showMessage(UserActivity.this, "Successfully subscribed.");
                            return;
                        }
                        if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                            UserActivity.this.recreate();
                        }
                        Log.i(TAG, "/user/{id}/subscribe: " + response.code() + " : " + response.body());
                    }
                });
            }
        });

        userViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(UserViewModel.class);
        userViewModel.fetchUserInformation(token, userId, this);
        userViewModel.getUserLiveData()
              .observe(this, user -> {
                  binding.userFullName.setText(user.getName() + " " + user.getSurname());
                  binding.userInfo.setText(user.getInfo());
                  String birthday = user.getBirthday();
                  binding.userBirthday.setText(birthday + " (" + getAgeFromDate(birthday) + ')');
                  binding.userGender.setText(genderResolver(this, user.getGender()));
                  //binding.userPhoto.setImageBitmap();
              });
        userViewModel.getAmISubscriber()
              .observe(this, val -> {
                  amISubscriber = val;
                  updateSubscribeButtonText();
              });
        userViewModel.getUserEventsLiveData()
              .observe(this, events -> adapter.setData(events));

        binding.eventsGrid.setLayoutManager(new GridLayoutManager(this, 2));
        binding.eventsGrid.setAdapter(adapter);
    }

    private void updateSubscribeButtonText() {
        if (amISubscriber == null) {
            binding.subscribeButton.setText("...");
        }
        if (amISubscriber) {
            binding.subscribeButton.setText("Unsubscribe");
        } else {
            binding.subscribeButton.setText("Subscribe");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
          @Nullable View parent, @NonNull String name, @NonNull Context ctx,
          @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, ctx, attrs);
    }

    public static void dispatchToUserActivity(Context ctx, String userId) {
        ctx.startActivity(createUserActivityIntent(ctx, userId));
    }

    private static Intent createUserActivityIntent(Context ctx, String userId) {
        return new Intent(ctx, UserActivity.class)
              .putExtra(USER_ID_KEY, userId);
    }
}
