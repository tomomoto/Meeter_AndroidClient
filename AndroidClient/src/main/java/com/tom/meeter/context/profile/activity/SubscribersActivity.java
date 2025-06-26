package com.tom.meeter.context.profile.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.user.activity.UserActivity.dispatchToUserActivity;
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
import com.tom.meeter.context.profile.factory.ProfileSubscribersViewModelAssistedFactory;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.context.profile.viewmodel.ProfileSubscribersViewModel;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.databinding.ActivityProfileSubscribersBinding;
import com.tom.meeter.infrastructure.components.binder.PhotoDownloaderWithCacheSubscriberBinder;

import javax.inject.Inject;

public class SubscribersActivity extends AppCompatActivity {

    private static final String TAG = SubscribersActivity.class.getCanonicalName();

    @Inject
    ProfileService profileService;
    @Inject
    TokenService tokenService;
    @Inject
    ProfileSubscribersViewModelAssistedFactory assistedFactory;
    @Inject
    ImageDownloader imgDownloader;
    @Inject
    UserService userService;

    private AccountManager accountManager;
    private ActivityProfileSubscribersBinding binding;
    private SubscribersAdapter adapter;
    private ProfileSubscribersViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logMethod(TAG, this);

        ((App) getApplication()).getComponent().inject(this);
        accountManager = AccountManager.get(this);

        checkToken(
              this::onInit, this::finish,
              accountManager, this, tokenService);
    }

    private void onInit(String token) {
        logMethod(TAG, this);

        binding = ActivityProfileSubscribersBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String auth = getAuthHeader(accountManager);
        adapter = new SubscribersAdapter(
              new PhotoDownloaderWithCacheSubscriberBinder(
                    this, imgDownloader,
                    (user) -> dispatchToUserActivity(this, user.getId()),
                    (sub, pos) -> adapter.onSubUnsubClick(
                          userService, sub, pos, this::recreate, this, auth),
                    this::recreate
              ));

        binding.recyclerSubscribers.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerSubscribers.setAdapter(adapter);

        viewModel = new ViewModelProvider(
              this,
              assistedFactory.factory(
                    assistedFactory, auth, this,
                    this::recreate))
              .get(ProfileSubscribersViewModel.class);

        viewModel.getSubscribers()
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
