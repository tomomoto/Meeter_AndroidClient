package com.tom.meeter.context.user.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.context.user.components.adapter.UsersAdapter;
import com.tom.meeter.context.user.factory.UserSubscriptionsAssistedFactory;
import com.tom.meeter.context.user.utils.Utils;
import com.tom.meeter.context.user.viewmodel.UserSubscriptionsViewModel;
import com.tom.meeter.databinding.ActivityProfileSubscriptionsBinding;

import javax.inject.Inject;

public class UserSubscriptionsActivity extends AppCompatActivity {

    private static final String TAG = UserSubscriptionsActivity.class.getCanonicalName();

    @Inject
    TokenService tokenService;
    @Inject
    UserSubscriptionsAssistedFactory assistedFactory;
    @Inject
    UsersAdapter adapter;

    private ActivityProfileSubscriptionsBinding binding;
    private UserSubscriptionsViewModel viewModel;
    private final Runnable onAuthFail = this::recreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        if (Utils.isIncorrect(this)) {
            return;
        }

        binding = ActivityProfileSubscriptionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ((App) getApplication()).getUserComponent().inject(this);

        adapter.initialize(this, onAuthFail);

        //setToken(accountManager, Launcher.EXPIRED);
        checkToken(this::onInit, this::finish, this, tokenService);
    }

    private void onInit(String token) {
        logMethod(TAG, this);

        viewModel = new ViewModelProvider(
              this,
              assistedFactory.factory(
                    assistedFactory,
                    Utils.getUserId(this),
                    this, onAuthFail))
              .get(UserSubscriptionsViewModel.class);

        binding.swipeRefreshLayout.setOnRefreshListener(() -> viewModel.init());

        binding.recyclerSubscriptions.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerSubscriptions.setAdapter(adapter);

        viewModel.getSubscriptions()
              .observe(this, subs -> {
                  binding.swipeRefreshLayout.setRefreshing(false);
                  adapter.setData(subs);
              });
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
              .putExtra(Utils.USER_ID_KEY, userId);
    }
}
