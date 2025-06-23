package com.tom.meeter.context.user.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.infrastructure.http.ActivityRecreatorOnAuthFailure;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class UserSubscribersViewModel extends ViewModel {

    private static final String TAG = UserSubscribersViewModel.class.getCanonicalName();

    private final MutableLiveData<List<UserDTO>> subscribersLiveData = new MutableLiveData<>();

    private final UserService userService;

    @Inject
    public UserSubscribersViewModel(UserService userService) {
        logMethod(TAG, this);
        this.userService = userService;
    }

    public void fetchUserSubscribers(String auth, String userId, Activity activity) {
        userService.getSubscribers(auth, userId).enqueue(
              new ActivityRecreatorOnAuthFailure<>(activity) {
                  @Override
                  public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK) {
                          subscribersLiveData.setValue(resp.body());
                          return;
                      }
                  }
              }
        );
    }

    public LiveData<List<UserDTO>> getSubscribersLiveData() {
        return subscribersLiveData;
    }
}
