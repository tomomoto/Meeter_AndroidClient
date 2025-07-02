package com.tom.meeter.context.profile.component.viewmodel;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.service.ProfileService;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import retrofit2.Call;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {

    private static final String TAG = ProfileViewModel.class.getCanonicalName();

    private final ProfileService profileService;
    private final ImageDownloader imageDownloader;
    private final Context ctx;
    private final Runnable onNotAuthenticated;

    private final MutableLiveData<UserDTO> profile = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> photo = new MutableLiveData<>();
    private final MutableLiveData<List<EventDTO>> events = new MutableLiveData<>();

    @AssistedInject
    public ProfileViewModel(
          ProfileService profileService, ImageDownloader imageDownloader,
          @Assisted Context ctx,
          @Assisted Runnable onNotAuthenticated) {
        logMethod(TAG, this);
        this.profileService = profileService;
        this.imageDownloader = imageDownloader;
        this.ctx = ctx.getApplicationContext();
        this.onNotAuthenticated = onNotAuthenticated;
        init();
    }

    public void init() {
        profileService.getProfile(getAuthHeader(AccountManager.get(ctx))).enqueue(
              new BaseOnNotAuthenticatedCallback<>(ctx, onNotAuthenticated) {
                  @Override
                  public void onResponse(Call<UserDTO> call, Response<UserDTO> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() != HttpCodes.OK) {
                          return;
                      }
                      UserDTO user = resp.body();
                      profile.postValue(user);
                      String photoPath = user.getPhotoPath();
                      if (photoPath == null) {
                          return;
                      }
                      imageDownloader.downloadUserImage(
                            photoPath, ctx,
                            ImagesHelper::bigCircleImage,
                            photo::setValue,
                            onNotAuthenticated);
                      return;
                  }
              }
        );
        profileService.getProfileEvents(getAuthHeader(AccountManager.get(ctx))).enqueue(
              new BaseOnNotAuthenticatedCallback<>(ctx, onNotAuthenticated) {
                  @Override
                  public void onResponse(
                        Call<List<EventDTO>> call, Response<List<EventDTO>> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() != HttpCodes.OK || resp.body() == null) {
                          return;
                      }
                      events.postValue(resp.body());
                      return;
                  }
              }
        );
    }

    @Override
    protected void onCleared() {
        logMethod(TAG, this);
        super.onCleared();
    }

    public LiveData<UserDTO> getProfile() {
        return profile;
    }

    public LiveData<List<EventDTO>> getEvents() {
        return events;
    }

    public LiveData<Bitmap> getPhoto() {
        return photo;
    }
}
