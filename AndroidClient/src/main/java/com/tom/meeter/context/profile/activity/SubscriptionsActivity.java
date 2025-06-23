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
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.profile.adapter.SubscribersAdapter;
import com.tom.meeter.context.profile.subscriber.Subscriber;
import com.tom.meeter.context.profile.viewmodel.ProfileSubscriptionsViewModel;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.databinding.ActivityProfileSubscriptionsBinding;
import com.tom.meeter.infrastructure.components.binder.PhotoDownloaderWithCacheSubscriberBinder;
import com.tom.meeter.infrastructure.http.ActivityRecreatorOnAuthFailure;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class SubscriptionsActivity extends AppCompatActivity {

    private static final String TAG = SubscribersActivity.class.getCanonicalName();

    @Inject
    TokenService tokenService;
    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    ImageDownloader imgDownloader;
    @Inject
    UserService userService;

    private ActivityProfileSubscriptionsBinding binding;

    private AccountManager accountManager;

    private SubscribersAdapter adapter;

    private ProfileSubscriptionsViewModel profileSubscriptionsViewModel;

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

        binding = ActivityProfileSubscriptionsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        adapter = new SubscribersAdapter(
              new PhotoDownloaderWithCacheSubscriberBinder(
                    this, imgDownloader,
                    (e) -> dispatchToUserActivity(this, e.getId()),
                    this::onSubUnsubClick,
                    this::recreate
              ));

        profileSubscriptionsViewModel = ViewModelProviders.of(this, viewModelFactory)
              .get(ProfileSubscriptionsViewModel.class);
        profileSubscriptionsViewModel.fetchProfileSubscriptions(getAuthHeader(accountManager), this);

        profileSubscriptionsViewModel.getSubscriptionsLiveData()
              .observe(this, subs -> adapter.setData(subs));
        binding.recyclerSubscriptions.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerSubscriptions.setAdapter(adapter);
    }

    private void onSubUnsubClick(Subscriber sub, int position) {
        if (sub.isAmISubscribedTo()) {
            userService.unsubscribe(AuthHelper.getAuthHeader(accountManager), sub.getUser().getId())
                  .enqueue(new ActivityRecreatorOnAuthFailure<>(this) {
                      @Override
                      public void onResponse(Call<Void> call, Response<Void> response) {
                          super.onResponse(call, response);
                          sub.setAmISubscribedTo(false);
                          adapter.notifyItemChanged(position);
                      }
                  });
        } else {
            userService.subscribe(AuthHelper.getAuthHeader(accountManager), sub.getUser().getId())
                  .enqueue(new ActivityRecreatorOnAuthFailure<>(this) {
                      @Override
                      public void onResponse(Call<Void> call, Response<Void> response) {
                          super.onResponse(call, response);
                          sub.setAmISubscribedTo(true);
                          adapter.notifyItemChanged(position);
                      }
                  });
        }
        return;
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
