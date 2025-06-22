package com.tom.meeter.context.profile.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.infrastructure.http.ActivityRecreatorOnAuthFailure;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileSubscribersViewModel extends ViewModel {

    private static final String TAG = ProfileSubscribersViewModel.class.getCanonicalName();

    private final MutableLiveData<List<UserDTO>> subscribersLiveData = new MutableLiveData<>();

    private final ProfileService profileService;

    @Inject
    public ProfileSubscribersViewModel(ProfileService profileService) {
        logMethod(TAG, this);
        this.profileService = profileService;
    }

    public void fetchProfileSubscribers(String auth, Activity activity) {
        profileService.getSubscribers(auth).enqueue(
              new ActivityRecreatorOnAuthFailure<>(activity) {
                  @Override
                  public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK && resp.body() != null) {
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
