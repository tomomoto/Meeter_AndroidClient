package com.tom.meeter.context.user.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.user.activity.UserActivity.dispatchToUserActivity;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.context.user.adapter.UsersAdapter;
import com.tom.meeter.context.user.viewmodel.UserSubscriptionsViewModel;
import com.tom.meeter.context.user.factory.UserViewModelFactory;
import com.tom.meeter.databinding.ActivityProfileSubscriptionsBinding;
import com.tom.meeter.infrastructure.components.binder.PhotoDownloaderWithCacheUserBinder;

import javax.inject.Inject;

public class UserSubscriptionsActivity extends AppCompatActivity {

    private static final String TAG = UserSubscriptionsActivity.class.getCanonicalName();

    @Inject
    TokenService tokenService;
    @Inject
    UserViewModelFactory viewModelFactory;
    @Inject
    ImageDownloader imgDownloader;

    private ActivityProfileSubscriptionsBinding binding;
    private UserSubscriptionsViewModel userSubscriptionsViewModel;
    private UsersAdapter adapter;
    private AccountManager accountManager;
    private String userId;

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
        userId = extras.getString(UserActivity.USER_ID_KEY);
        if (userId == null) {
            Log.d(TAG, "Unable to create user activity without 'user_id' provided.");
            finish();
            return;
        }

        ((App) getApplication()).getUserComponent().inject(this);
        accountManager = AccountManager.get(this);

        adapter = new UsersAdapter(
              new PhotoDownloaderWithCacheUserBinder(
                    this, imgDownloader,
                    user -> dispatchToUserActivity(this, user.getId()),
                    this::recreate));

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken(this::onInit, this::finish, accountManager, this, tokenService);
    }

    private void onInit(String token) {
        logMethod(TAG, this);
        binding = ActivityProfileSubscriptionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        userSubscriptionsViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(UserSubscriptionsViewModel.class);
        userSubscriptionsViewModel.fetchUserSubscriptions(getAuthHeader(accountManager), userId, this);

        userSubscriptionsViewModel.getSubscriptionsLiveData()
              .observe(this, subs -> adapter.setData(subs));
        binding.recyclerSubscriptions.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerSubscriptions.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(
          @Nullable View parent, @NonNull String name, @NonNull Context ctx,
          @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, ctx, attrs);
    }

    @Override
    protected void onStart() {
        logMethod(TAG, this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        logMethod(TAG, this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        logMethod(TAG, this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        logMethod(TAG, this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        logMethod(TAG, this);
        super.onResume();
    }

    @Override
    protected void onRestart() {
        logMethod(TAG, this);
        super.onRestart();
    }

    public static void dispatchToUserSubscriptionsActivity(Context ctx, String userId) {
        ctx.startActivity(createUserSubscriptionsActivityIntent(ctx, userId));
    }

    private static Intent createUserSubscriptionsActivityIntent(Context ctx, String userId) {
        return new Intent(ctx, UserSubscriptionsActivity.class)
              .putExtra(UserActivity.USER_ID_KEY, userId);
    }
}
