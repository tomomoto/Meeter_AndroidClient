package com.tom.meeter.context.profile.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.factory.ProfileViewModelAssistedFactory;
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

    private final MutableLiveData<UserDTO> profile = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> photo = new MutableLiveData<>();
    private final MutableLiveData<List<EventDTO>> events = new MutableLiveData<>();

    private final ProfileService profileService;
    private final ImageDownloader imageDownloader;

    private final String auth;
    private final Context ctx;
    private final Runnable onNotAuthenticated;

    @AssistedInject
    public ProfileViewModel(
          ProfileService profileService, ImageDownloader imageDownloader,
          @Assisted String auth,
          @Assisted Context ctx,
          @Assisted Runnable onNotAuthenticated) {
        logMethod(TAG, this);
        this.profileService = profileService;
        this.imageDownloader = imageDownloader;
        this.auth = auth;
        this.ctx = ctx.getApplicationContext();
        this.onNotAuthenticated = onNotAuthenticated;
        init();
    }

    public static ViewModelProvider.Factory factory(
          ProfileViewModelAssistedFactory assistedFactory,
          String auth, Context ctx, Runnable onNotAuthenticated) {
        return new ViewModelProvider.Factory() {
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) assistedFactory.create(auth, ctx, onNotAuthenticated);
            }
        };
    }

    public void init() {
        profileService.getProfile(auth).enqueue(
              new BaseOnNotAuthenticatedCallback<>(ctx, onNotAuthenticated) {
                  @Override
                  public void onResponse(Call<UserDTO> call, Response<UserDTO> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() != HttpCodes.OK) {
                          return;
                      }
                      UserDTO user = resp.body();
                      profile.setValue(user);
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
        profileService.getProfileEvents(auth).enqueue(
              new BaseOnNotAuthenticatedCallback<>(ctx, onNotAuthenticated) {
                  @Override
                  public void onResponse(
                        Call<List<EventDTO>> call, Response<List<EventDTO>> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() != HttpCodes.OK || resp.body() == null) {
                          return;
                      }
                      events.setValue(resp.body());
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
