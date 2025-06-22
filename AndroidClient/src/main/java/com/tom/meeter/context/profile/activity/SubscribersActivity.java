package com.tom.meeter.context.profile.activity;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.checkToken;
import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.user.activity.UserActivity.dispatchToUserActivity;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tom.meeter.App;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.profile.adapter.UsersAdapter;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.context.profile.viewmodel.ProfileSubscribersViewModel;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.databinding.ActivityProfileSubscribersBinding;
import com.tom.meeter.infrastructure.components.binder.PhotoDownloaderWithCacheUserBinder;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

public class SubscribersActivity extends AppCompatActivity {

    private static final String TAG = SubscribersActivity.class.getCanonicalName();

    @Inject
    ProfileService profileService;
    @Inject
    TokenService tokenService;

    ActivityProfileSubscribersBinding binding;

    private AccountManager accountManager;

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    ImageDownloader imgDownloader;

    private UsersAdapter adapter;

    private ProfileSubscribersViewModel profileSubscribersViewModel;

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

        adapter = new UsersAdapter(
              new PhotoDownloaderWithCacheUserBinder(
                    this, imgDownloader,
                    (e) -> dispatchToUserActivity(this, e.getId()),
                    onSubscriberButtonClick(),
                    this::recreate
              ));

        profileSubscribersViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(ProfileSubscribersViewModel.class);
        profileSubscribersViewModel.fetchProfileSubscribers(getAuthHeader(accountManager), this);

        profileSubscribersViewModel.getSubscribersLiveData()
              .observe(this, subs -> adapter.setData(subs));
        binding.recyclerSubscribers.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerSubscribers.setAdapter(adapter);
    }

    private static View.OnClickListener onSubscriberButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
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
