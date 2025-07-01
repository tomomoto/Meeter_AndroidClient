package com.tom.meeter.context.profile.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.profile.adapter.SubscribersAdapter;
import com.tom.meeter.context.profile.factory.ProfileSubscriptionsAssistedFactory;
import com.tom.meeter.context.profile.viewmodel.ProfileSubscriptionsViewModel;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.databinding.ActivityProfileSubscriptionsBinding;

import javax.inject.Inject;

public class SubscriptionsActivity extends AppCompatActivity {

    private static final String TAG = SubscribersActivity.class.getCanonicalName();

    @Inject
    TokenService tokenService;
    @Inject
    ProfileSubscriptionsAssistedFactory assistedFactory;
    @Inject
    ImageDownloader imgDownloader;
    @Inject
    UserService userService;
    @Inject
    SubscribersAdapter adapter;

    private final Runnable onAuthFail = this::recreate;
    private ActivityProfileSubscriptionsBinding binding;
    private AccountManager accountManager;
    private ProfileSubscriptionsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        ((App) getApplication()).getProfileComponent().inject(this);
        accountManager = AccountManager.get(this);

        checkToken(
              (token) -> onInit(), this::finish,
              accountManager, this, tokenService);
    }

    private void onInit() {
        logMethod(TAG, this);

        binding = ActivityProfileSubscriptionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        adapter.initialize(this, onAuthFail);

        binding.recyclerSubscriptions.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerSubscriptions.setAdapter(adapter);

        viewModel = new ViewModelProvider(
              this,
              assistedFactory.factory(assistedFactory, this, onAuthFail))
              .get(ProfileSubscriptionsViewModel.class);

        viewModel.getSubscriptions()
              .observe(this, subs -> adapter.setData(subs));
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
}
