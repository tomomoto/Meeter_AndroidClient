package com.tom.meeter.context.user.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.HttpCodes;
import com.tom.meeter.infrastructure.http.HttpErrorLogger;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class UserViewModel extends ViewModel {

    private static final String TAG = UserViewModel.class.getCanonicalName();

    private final MutableLiveData<UserDTO> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> amISubscriber = new MutableLiveData<>();
    private final MutableLiveData<List<EventDTO>> userEventsLiveData = new MutableLiveData<>();

    private final UserService userService;

    @Inject
    public UserViewModel(UserService userService) {
        logMethod(TAG, this);
        this.userService = userService;
    }

    public void fetchUserInformation(String token, String userId, Activity activity) {
        userService.getUser(Globals.getAuthHeader(token), userId).enqueue(
              new HttpErrorLogger<>(activity) {
                  @Override
                  public void onResponse(Call<UserDTO> call, Response<UserDTO> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK && resp.body() != null) {
                          userLiveData.setValue(resp.body());
                          return;
                      }
                      if (resp.code() == HttpCodes.NOT_AUTHENTICATED) {
                          activity.recreate();
                      }
                  }
              }
        );

        userService.amISubscribed(Globals.getAuthHeader(token), userId).enqueue(
              new HttpErrorLogger<>(activity) {
                  @Override
                  public void onResponse(Call<Boolean> call, Response<Boolean> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK && resp.body() != null) {
                          amISubscriber.setValue(resp.body());
                          return;
                      }
                      if (resp.code() == HttpCodes.NOT_AUTHENTICATED) {
                          activity.recreate();
                      }
                  }
              }
        );

        userService.getUserEvents(Globals.getAuthHeader(token), userId).enqueue(
              new HttpErrorLogger<>(activity) {
                  @Override
                  public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK && resp.body() != null) {
                          userEventsLiveData.setValue(resp.body());
                          return;
                      }
                      if (resp.code() == HttpCodes.NOT_AUTHENTICATED) {
                          activity.recreate();
                      }
                  }
              });
    }

    @Override
    protected void onCleared() {
        logMethod(TAG, this);
        super.onCleared();
    }

    public LiveData<UserDTO> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<List<EventDTO>> getUserEventsLiveData() {
        return userEventsLiveData;
    }

    public MutableLiveData<Boolean> getAmISubscriber() {
        return amISubscriber;
    }
}
