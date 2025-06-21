package com.tom.meeter.context.profile.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.context.profile.user.domain.User;
import com.tom.meeter.infrastructure.http.ActivityRestarterOnAuthFailure;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {

    private static final String TAG = ProfileViewModel.class.getCanonicalName();

    private final MutableLiveData<User> profileLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<EventDTO>> profileEventsLiveData = new MutableLiveData<>();

    private final ProfileService profileService;

    @Inject
    public ProfileViewModel(ProfileService profileService) {
        logMethod(TAG, this);
        this.profileService = profileService;
    }

    public void fetchProfile(String auth, Fragment fragment) {
        profileService.getProfile(auth).enqueue(
              new ActivityRestarterOnAuthFailure<>(fragment) {
                  @Override
                  public void onResponse(Call<User> call, Response<User> response) {
                      super.onResponse(call, response);
                      if (response.code() == HttpCodes.OK && response.body() != null) {
                          profileLiveData.setValue(response.body());
                          return;
                      }
                      Log.i(TAG, "/profile: " + response.code() + " : " + response.body());
                  }
              }
        );
        profileService.getProfileEvents(auth).enqueue(
              new ActivityRestarterOnAuthFailure<>(fragment) {
                  @Override
                  public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> response) {
                      super.onResponse(call, response);
                      if (response.code() == HttpCodes.OK && response.body() != null) {
                          profileEventsLiveData.setValue(response.body());
                          return;
                      }
                      Log.i(TAG, "/profile/events: " + response.code() + " : " + response.body());
                  }
              }
        );
    }

    @Override
    protected void onCleared() {
        logMethod(TAG, this);
        super.onCleared();
    }

    public LiveData<User> getProfileLiveData() {
        return profileLiveData;
    }

    public LiveData<List<EventDTO>> getProfileEventsLiveData() {
        return profileEventsLiveData;
    }
}
