package com.tom.meeter.context.profile.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.profile.event.domain.Event;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.ActivityRestarterOnAuthFailure;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileEventsViewModel extends ViewModel {

    private static final String TAG = ProfileEventsViewModel.class.getCanonicalName();

    private final MutableLiveData<List<Event>> profileEventsLiveData = new MutableLiveData<>();

    private final ProfileService profileService;

    @Inject
    public ProfileEventsViewModel(ProfileService profileService) {
        logMethod(TAG, this);
        this.profileService = profileService;
    }

    public void getProfileEvents(String token, Fragment fragment) {
        profileService.getProfileEvents(Globals.getAuthHeader(token)).enqueue(
              new ActivityRestarterOnAuthFailure<>(fragment) {
                  @Override
                  public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
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

    public LiveData<List<Event>> getProfileEventsLiveData() {
        return profileEventsLiveData;
    }
}
