package com.tom.meeter.context.user.viewmodel;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import retrofit2.Call;
import retrofit2.Response;

public class UserSubscribersViewModel extends ViewModel {

    private static final String TAG = UserSubscribersViewModel.class.getCanonicalName();

    private final UserService service;
    private final String userId;
    private final Context ctx;
    private final Runnable onNotAuthenticated;

    private final MutableLiveData<List<UserDTO>> subscribers = new MutableLiveData<>();

    @AssistedInject
    public UserSubscribersViewModel(
          UserService service,
          @Assisted String userId,
          @Assisted Context ctx,
          @Assisted Runnable onNotAuthenticated) {
        logMethod(TAG, this);
        this.service = service;
        this.userId = userId;
        this.ctx = ctx.getApplicationContext();
        this.onNotAuthenticated = onNotAuthenticated;
        init();
    }

    public void init() {
        service.getSubscribers(getAuthHeader(AccountManager.get(ctx)), userId).enqueue(
              new BaseOnNotAuthenticatedCallback<>(ctx, onNotAuthenticated) {
                  @Override
                  public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() != HttpCodes.OK || resp.body() == null) {
                          return;
                      }
                      subscribers.setValue(resp.body());
                      return;
                  }
              }
        );
    }

    public LiveData<List<UserDTO>> getSubscribers() {
        return subscribers;
    }
}
