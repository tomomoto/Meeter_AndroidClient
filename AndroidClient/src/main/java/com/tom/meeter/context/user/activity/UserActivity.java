package com.tom.meeter.context.user.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.event.activity.EventActivity.dispatchToEventActivity;
import static com.tom.meeter.context.user.activity.UserSubscribersActivity.dispatchToUserSubscribersActivity;
import static com.tom.meeter.context.user.activity.UserSubscriptionsActivity.dispatchToUserSubscriptionsActivity;
import static com.tom.meeter.infrastructure.common.CommonHelper.EMPTY_STR;
import static com.tom.meeter.infrastructure.common.CommonHelper.genderResolver;
import static com.tom.meeter.infrastructure.common.DateHelper.getAgeFromDate;
import static com.tom.meeter.infrastructure.common.ImagesHelper.circleImage;
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
import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.profile.activity.ProfileActivity;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.context.user.viewmodel.UserViewModel;
import com.tom.meeter.databinding.ActivityUserBinding;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.components.adapter.EventsCardAdapter;
import com.tom.meeter.infrastructure.components.binder.PhotoDownloaderEventBinder;
import com.tom.meeter.infrastructure.http.HttpCodes;
import com.tom.meeter.infrastructure.http.HttpErrorLogger;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import java.time.LocalDate;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = UserActivity.class.getCanonicalName();
    private static final String USER_ID_KEY = "user_id";

    @Inject
    TokenService tokenService;
    @Inject
    UserService userService;
    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    ImageDownloader imgDownloader;
    private ActivityUserBinding binding;
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
        accountManager = AccountManager.get(this);
        if (userId.equals(AuthHelper.getUserUuid(accountManager))) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            return;
        }

        ((App) getApplication()).getUserComponent().inject(this);

        adapter = new EventsCardAdapter(
              new PhotoDownloaderEventBinder(
                    this, imgDownloader,
                    event -> dispatchToEventActivity(this, event.getId()), this::recreate));

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken(this::onInit, this::finish, accountManager, this, tokenService);
    }

    private void onInit(String token) {
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.subscribeBtn.setOnClickListener(v -> {
            if (amISubscriber == null) {
                // As not initialized atm...
                return;
            }
            if (amISubscriber) {
                userService.unsubscribe(Globals.getAuthHeader(token), userId).enqueue(
                      new HttpErrorLogger<>(this) {
                          @Override
                          public void onResponse(Call<Void> call, Response<Void> resp) {
                              super.onResponse(call, resp);
                              if (resp.code() == HttpCodes.OK) {
                                  amISubscriber = false;
                                  updateSubscribeButtonText();
                                  showMessage(UserActivity.this, R.string.successfully_unsubscribed);
                                  return;
                              }
                              if (resp.code() == HttpCodes.NOT_AUTHENTICATED) {
                                  UserActivity.this.recreate();
                              }
                          }
                      });
            } else {
                userService.subscribe(Globals.getAuthHeader(token), userId).enqueue(
                      new HttpErrorLogger<>(this) {
                          @Override
                          public void onResponse(Call<Void> call, Response<Void> resp) {
                              super.onResponse(call, resp);
                              if (resp.code() == HttpCodes.OK) {
                                  amISubscriber = true;
                                  updateSubscribeButtonText();
                                  showMessage(UserActivity.this, R.string.successfully_subscribed);
                                  return;
                              }
                              if (resp.code() == HttpCodes.NOT_AUTHENTICATED) {
                                  UserActivity.this.recreate();
                              }
                          }
                      });
            }
        });

        userViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(UserViewModel.class);
        userViewModel.fetchUserInformation(token, userId, this);
        userViewModel.getUserLiveData()
              .observe(this, user -> {
                  binding.name.setText(user.getName());
                  binding.gender.setText(genderResolver(getApplicationContext(), user.getGender()));

                  binding.surname.setText(user.getSurname());
                  LocalDate birthday = user.getBirthday();
                  binding.birthday.setText(birthday == null ? EMPTY_STR : birthday.toString());
                  binding.age.setText(getString(R.string.profile_age_format, getAgeFromDate(birthday)));
                  binding.info.setText(user.getInfo());
              });
        userViewModel.getAmISubscriber()
              .observe(this, val -> {
                  amISubscriber = val;
                  updateSubscribeButtonText();
              });
        userViewModel.getUserEventsLiveData()
              .observe(this, events -> adapter.setData(events));
        userViewModel.getUserPhotoLiveData()
              .observe(
                    this,
                    photo -> binding.photo.setImageBitmap(circleImage(photo, 600, 600)));

        binding.events.setLayoutManager(new GridLayoutManager(this, 2));
        binding.events.setAdapter(adapter);

        binding.subscribers.setOnClickListener(
              v -> dispatchToUserSubscribersActivity(this, userId));
        binding.subscriptions.setOnClickListener(
              v -> dispatchToUserSubscriptionsActivity(this, userId));
    }

    private void updateSubscribeButtonText() {
        if (amISubscriber == null) {
            binding.subscribeBtn.setText("...");
        }
        if (amISubscriber) {
            binding.subscribeBtn.setText(R.string.unsubscribe);
        } else {
            binding.subscribeBtn.setText(R.string.subscribe);
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
