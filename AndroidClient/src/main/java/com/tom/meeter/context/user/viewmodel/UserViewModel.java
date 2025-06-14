package com.tom.meeter.context.user.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.profile.event.domain.Event;
import com.tom.meeter.context.profile.user.domain.User;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.infrastructure.common.Constants;
import com.tom.meeter.infrastructure.http.DisconnectLogger;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class UserViewModel extends ViewModel {

    private static final String TAG = UserViewModel.class.getCanonicalName();

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> userEventsLiveData = new MutableLiveData<>();

    private final UserService userService;

    @Inject
    public UserViewModel(UserService userService) {
        logMethod(TAG, this);
        this.userService = userService;
    }

    public void fetchUserInformation(String token, String userId, Activity activity) {
        userService.getUser(Constants.getAuthHeader(token), userId).enqueue(
              new DisconnectLogger<>(activity) {
                  @Override
                  public void onResponse(Call<User> call, Response<User> response) {
                      if (response.code() == HttpCodes.OK && response.body() != null) {
                          userLiveData.setValue(response.body());
                          return;
                      }
                      if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                          activity.recreate();
                      }
                      Log.i(TAG, "/user/{id}: " + response.code() + " : " + response.body());
                  }
              }
        );

        userService.getUserEvents(Constants.getAuthHeader(token), userId).enqueue(
              new DisconnectLogger<>(activity) {
                  @Override
                  public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                      if (response.code() == HttpCodes.OK && response.body() != null) {
                          userEventsLiveData.setValue(response.body());
                          return;
                      }
                      if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                          activity.recreate();
                      }
                      Log.i(TAG, "/user/{id}/events: " + response.code() + " : " + response.body());
                  }
              }
        );
    }

    @Override
    protected void onCleared() {
        logMethod(TAG, this);
        super.onCleared();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<List<Event>> getUserEventsLiveData() {
        return userEventsLiveData;
    }
}

