package com.tom.meeter.context.user.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class UserViewModel extends ViewModel {

    private static final String TAG = UserViewModel.class.getCanonicalName();

    private final MutableLiveData<UserDTO> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> amISubscriber = new MutableLiveData<>();
    private final MutableLiveData<List<EventDTO>> userEventsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ResponseBody> userPhotoLiveData = new MutableLiveData<>();

    private final UserService userService;
    private final ImageDownloader imgDownloader;

    @Inject
    public UserViewModel(
          UserService userService, ImageDownloader imgDownloader) {
        logMethod(TAG, this);
        this.imgDownloader = imgDownloader;
        this.userService = userService;
    }

    public void fetchUserInformation(String auth, String userId, Activity activity) {
        userService.getUser(auth, userId).enqueue(
              new BaseOnNotAuthenticatedCallback<>(activity, activity::recreate) {
                  @Override
                  public void onResponse(Call<UserDTO> call, Response<UserDTO> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK) {
                          UserDTO user = resp.body();
                          userLiveData.setValue(user);
                          String photoPath = user.getPhotoPath();
                          if (photoPath == null) {
                              return;
                          }
                          imgDownloader.downloadUserImage(
                                photoPath, activity.getApplicationContext(),
                                userPhotoLiveData::setValue,
                                activity::recreate);
                          return;
                      }
                  }
              }
        );

        userService.amISubscribed(auth, userId).enqueue(
              new BaseOnNotAuthenticatedCallback<>(activity, activity::recreate) {
                  @Override
                  public void onResponse(Call<Boolean> call, Response<Boolean> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK) {
                          amISubscriber.setValue(resp.body());
                          return;
                      }
                  }
              }
        );

        userService.getUserEvents(auth, userId).enqueue(
              new BaseOnNotAuthenticatedCallback<>(activity, activity::recreate) {
                  @Override
                  public void onResponse(Call<List<EventDTO>> call, Response<List<EventDTO>> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() == HttpCodes.OK) {
                          userEventsLiveData.setValue(resp.body());
                          return;
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

    public LiveData<Boolean> getAmISubscriber() {
        return amISubscriber;
    }

    public LiveData<ResponseBody> getUserPhotoLiveData() {
        return userPhotoLiveData;
    }
}
