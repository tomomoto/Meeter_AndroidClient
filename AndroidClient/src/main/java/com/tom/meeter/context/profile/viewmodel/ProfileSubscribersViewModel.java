package com.tom.meeter.context.profile.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.context.profile.subscriber.Subscriber;
import com.tom.meeter.infrastructure.http.ActivityRecreatorOnAuthFailure;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileSubscribersViewModel extends ViewModel {

    private static final String TAG = ProfileSubscribersViewModel.class.getCanonicalName();

    private final MutableLiveData<List<Subscriber>> subscribersLiveData = new MutableLiveData<>();

    private final ProfileService profileService;

    @Inject
    public ProfileSubscribersViewModel(ProfileService profileService) {
        logMethod(TAG, this);
        this.profileService = profileService;
    }

    public void fetchProfileSubscribers(String auth, Activity activity) {
        profileService.getMySubscriptions(auth).enqueue(
              new ActivityRecreatorOnAuthFailure<>(activity) {
                  @Override
                  public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK) {
                          getSubscribers(
                                auth,
                                activity,
                                resp.body()
                                      .stream()
                                      .collect(Collectors.toMap(
                                            UserDTO::getId, item -> item)));
                          return;
                      }
                  }
              }
        );
    }

    private void getSubscribers(
          String auth, Activity activity, Map<String, UserDTO> mySubscriptions) {
        profileService.getMySubscribers(auth).enqueue(
              new ActivityRecreatorOnAuthFailure<>(activity) {
                  @Override
                  public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK) {
                          List<Subscriber> subscribers = new ArrayList<>();
                          for (UserDTO subscriber : resp.body()) {
                              subscribers.add(
                                    new Subscriber(
                                          subscriber,
                                          mySubscriptions.get(subscriber.getId()) != null));
                          }
                          subscribersLiveData.setValue(subscribers);
                          return;
                      }
                  }
              }
        );
    }

    public LiveData<List<Subscriber>> getSubscribersLiveData() {
        return subscribersLiveData;
    }
}
