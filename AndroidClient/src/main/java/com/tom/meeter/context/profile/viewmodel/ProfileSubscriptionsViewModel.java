package com.tom.meeter.context.profile.viewmodel;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.context.profile.subscriber.Subscriber;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.ArrayList;
import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import retrofit2.Call;
import retrofit2.Response;

public class ProfileSubscriptionsViewModel extends ViewModel {

    private static final String TAG = ProfileSubscribersViewModel.class.getCanonicalName();

    private final ProfileService service;
    private final Context ctx;
    private final Runnable onNotAuthenticated;

    private final MutableLiveData<List<Subscriber>> subscriptions = new MutableLiveData<>();

    @AssistedInject
    public ProfileSubscriptionsViewModel(
          ProfileService service,
          @Assisted Context ctx,
          @Assisted Runnable onNotAuthenticated) {
        logMethod(TAG, this);
        this.service = service;
        this.ctx = ctx.getApplicationContext();
        this.onNotAuthenticated = onNotAuthenticated;
        init();
    }

    public void init() {
        service.getMySubscriptions(getAuthHeader(AccountManager.get(ctx))).enqueue(
              new BaseOnNotAuthenticatedCallback<>(ctx, onNotAuthenticated) {
                  @Override
                  public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() != HttpCodes.OK || resp.body() == null) {
                          return;
                      }
                      List<Subscriber> result = new ArrayList<>();
                      for (UserDTO subscription : resp.body()) {
                          result.add(new Subscriber(subscription, true));
                      }
                      subscriptions.setValue(result);
                      return;
                  }
              }
        );
    }

    public LiveData<List<Subscriber>> getSubscriptions() {
        return subscriptions;
    }
}
