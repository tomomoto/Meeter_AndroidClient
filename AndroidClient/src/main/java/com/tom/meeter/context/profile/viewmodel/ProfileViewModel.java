package com.tom.meeter.context.profile.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.profile.user.domain.User;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.infrastructure.common.Constants;
import com.tom.meeter.infrastructure.http.ActivityRestarterOnAuthFailure;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {

    private static final String TAG = ProfileViewModel.class.getCanonicalName();

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    private final ProfileService profileService;

    @Inject
    public ProfileViewModel(ProfileService profileService) {
        logMethod(TAG, this);
        this.profileService = profileService;
    }

    public void getProfile(String token, Fragment fragment) {
        profileService.getProfile(Constants.getAuthHeader(token)).enqueue(
              new ActivityRestarterOnAuthFailure<>(fragment) {
                  @Override
                  public void onResponse(Call<User> call, Response<User> response) {
                      super.onResponse(call, response);
                      if (response.code() == HttpCodes.OK && response.body() != null) {
                          userLiveData.setValue(response.body());
                          return;
                      }
                      Log.i(TAG, "/profile: " + response.code() + " : " + response.body());
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
}

