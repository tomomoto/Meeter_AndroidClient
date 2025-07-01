package com.tom.meeter.context.user.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.event.activity.EventDispatcherActivity.dispatchToEventActivity;
import static com.tom.meeter.context.user.activity.UserSubscribersActivity.dispatchToUserSubscribersActivity;
import static com.tom.meeter.context.user.activity.UserSubscriptionsActivity.dispatchToUserSubscriptionsActivity;
import static com.tom.meeter.infrastructure.common.CommonHelper.EMPTY_STR;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.R;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.profile.activity.ProfileActivity;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.context.user.factory.UserAssistedFactory;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.context.user.viewmodel.UserViewModel;
import com.tom.meeter.databinding.ActivityUserBinding;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.components.adapter.EventsCardAdapter;
import com.tom.meeter.infrastructure.components.binder.SimpleEventBinderImpl;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.time.LocalDate;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = UserActivity.class.getCanonicalName();
    public static final String USER_ID_KEY = "user_id";

    @Inject
    TokenService tokenService;
    @Inject
    UserService userService;
    @Inject
    UserAssistedFactory assistedFactory;
    @Inject
    ImageDownloader imgDownloader;
    @Inject
    EventsCardAdapter adapter;

    private ActivityUserBinding binding;
    private UserViewModel viewModel;
    private String userId;
    private AccountManager accountManager;
    private Boolean amISubscriber;
    private final Runnable onAuthFail = this::recreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        if (!validate()) {
            return;
        }

        ((App) getApplication()).getUserComponent().inject(this);

        adapter.setupBinder(
              this, onAuthFail,
              event -> dispatchToEventActivity(this, event.getId()));

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken(this::onInit, this::finish, accountManager, this, tokenService);
    }

    private boolean validate() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d(TAG, "Unable to create user activity without extras.");
            finish();
            return false;
        }
        userId = extras.getString(USER_ID_KEY);
        if (userId == null) {
            Log.d(TAG, "Unable to create user activity without 'user_id' provided.");
            finish();
            return false;
        }
        accountManager = AccountManager.get(this);
        if (userId.equals(AuthHelper.getUserUuid(accountManager))) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
            return false;
        }
        return true;
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
                      new BaseOnNotAuthenticatedCallback<>(this, onAuthFail) {
                          @Override
                          public void onResponse(Call<Void> call, Response<Void> resp) {
                              super.onResponse(call, resp);
                              if (resp.code() == HttpCodes.OK) {
                                  updateAmISubscriber(false);
                                  showMessage(UserActivity.this, R.string.successfully_unsubscribed);
                                  return;
                              }
                          }
                      });
            } else {
                userService.subscribe(Globals.getAuthHeader(token), userId).enqueue(
                      new BaseOnNotAuthenticatedCallback<>(this, onAuthFail) {
                          @Override
                          public void onResponse(Call<Void> call, Response<Void> resp) {
                              super.onResponse(call, resp);
                              if (resp.code() == HttpCodes.OK) {
                                  updateAmISubscriber(true);
                                  showMessage(UserActivity.this, R.string.successfully_subscribed);
                                  return;
                              }
                          }
                      });
            }
        });

        viewModel = new ViewModelProvider(
              this,
              assistedFactory.factory(
                    assistedFactory, userId, this, onAuthFail))
              .get(UserViewModel.class);

        binding.events.setLayoutManager(new GridLayoutManager(this, 2));
        binding.events.setAdapter(adapter);

        viewModel.getUser()
              .observe(this, user -> {
                  binding.name.setText(user.getName());
                  binding.gender.setText(genderResolver(getApplicationContext(), user.getGender()));

                  binding.surname.setText(user.getSurname());
                  LocalDate birthday = user.getBirthday();
                  binding.birthday.setText(birthday == null ? EMPTY_STR : birthday.toString());
                  binding.age.setText(getString(R.string.profile_age_format, getAgeFromDate(birthday)));
                  binding.info.setText(user.getInfo());
              });
        viewModel.getAmISubscriber()
              .observe(this, this::updateAmISubscriber);
        viewModel.getEvents()
              .observe(this, events -> adapter.setData(events));
        viewModel.getPhoto()
              .observe(
                    this,
                    photo -> binding.photo.setImageBitmap(photo));

        binding.subscribers.setOnClickListener(
              v -> dispatchToUserSubscribersActivity(this, userId));
        binding.subscriptions.setOnClickListener(
              v -> dispatchToUserSubscriptionsActivity(this, userId));
    }

    private void updateAmISubscriber(boolean value) {
        amISubscriber = value;
        binding.subscribeBtn.setText(amISubscriber ? R.string.unsubscribe : R.string.subscribe);
    }

    @Nullable
    @Override
    public View onCreateView(
          @Nullable View parent, @NonNull String name, @NonNull Context ctx,
          @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, ctx, attrs);
    }

    @Override
    protected void onDestroy() {
        logMethod(TAG, this);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        logMethod(TAG, this);
        super.onStop();
    }

    @Override
    protected void onPause() {
        logMethod(TAG, this);
        super.onPause();
    }


    public static void dispatchToUserActivity(Context ctx, String userId) {
        ctx.startActivity(createUserActivityIntent(ctx, userId));
    }

    public static Intent createUserActivityIntent(Context ctx, String userId) {
        return new Intent(ctx, UserActivity.class)
              .putExtra(USER_ID_KEY, userId);
    }
}
