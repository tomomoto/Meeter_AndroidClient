package com.tom.meeter.context.profile.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Activity;

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

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileSubscriptionsViewModel extends ViewModel {

    private static final String TAG = ProfileSubscribersViewModel.class.getCanonicalName();

    private final MutableLiveData<List<Subscriber>> subscriptionsLiveData = new MutableLiveData<>();

    private final ProfileService profileService;

    @Inject
    public ProfileSubscriptionsViewModel(ProfileService profileService) {
        logMethod(TAG, this);
        this.profileService = profileService;
    }

    public void fetchProfileSubscriptions(String auth, Activity activity) {
        profileService.getMySubscriptions(auth).enqueue(
              new BaseOnNotAuthenticatedCallback<>(activity, activity::recreate) {
                  @Override
                  public void onResponse(Call<List<UserDTO>> call, Response<List<UserDTO>> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK) {
                          List<Subscriber> subscribers = new ArrayList<>();
                          for (UserDTO subscription : resp.body()) {
                              subscribers.add(new Subscriber(subscription, true));
                          }
                          subscriptionsLiveData.setValue(subscribers);
                          return;
                      }
                  }
              }
        );
    }

    public LiveData<List<Subscriber>> getSubscriptionsLiveData() {
        return subscriptionsLiveData;
    }
}
