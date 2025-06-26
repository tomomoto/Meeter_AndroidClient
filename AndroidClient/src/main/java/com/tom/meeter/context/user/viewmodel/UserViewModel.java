package com.tom.meeter.context.user.viewmodel;

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
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import retrofit2.Call;
import retrofit2.Response;

public class UserViewModel extends ViewModel {

    private static final String TAG = UserViewModel.class.getCanonicalName();

    private final UserService service;
    private final ImageDownloader imgDownloader;
    private final String userId;
    private final Context ctx;
    private final Runnable onNotAuthenticated;

    private final MutableLiveData<UserDTO> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> amISubscriber = new MutableLiveData<>();
    private final MutableLiveData<List<EventDTO>> events = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> photo = new MutableLiveData<>();

    @AssistedInject
    public UserViewModel(
          UserService service, ImageDownloader imgDownloader,
          @Assisted String userId,
          @Assisted Context ctx,
          @Assisted Runnable onNotAuthenticated) {
        logMethod(TAG, this);
        this.service = service;
        this.imgDownloader = imgDownloader;
        this.userId = userId;
        this.ctx = ctx.getApplicationContext();
        this.onNotAuthenticated = onNotAuthenticated;
        init();
    }

    public void init() {
        service.getUser(getAuthHeader(AccountManager.get(ctx)), userId).enqueue(
              new BaseOnNotAuthenticatedCallback<>(ctx, onNotAuthenticated) {
                  @Override
                  public void onResponse(
                        Call<UserDTO> call, Response<UserDTO> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() != HttpCodes.OK || resp.body() == null) {
                          return;
                      }
                      UserDTO user = resp.body();
                      UserViewModel.this.user.setValue(user);
                      String photoPath = user.getPhotoPath();
                      if (photoPath == null) {
                          return;
                      }
                      imgDownloader.downloadUserImage(
                            photoPath, ctx,
                            ImagesHelper::bigCircleImage,
                            photo::setValue,
                            onNotAuthenticated);
                      return;
                  }
              }
        );

        service.amISubscribed(getAuthHeader(AccountManager.get(ctx)), userId).enqueue(
              new BaseOnNotAuthenticatedCallback<>(ctx, onNotAuthenticated) {
                  @Override
                  public void onResponse(
                        Call<Boolean> call, Response<Boolean> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() != HttpCodes.OK) {
                          return;
                      }
                      amISubscriber.setValue(resp.body());
                      return;
                  }
              }
        );

        service.getUserEvents(getAuthHeader(AccountManager.get(ctx)), userId).enqueue(
              new BaseOnNotAuthenticatedCallback<>(ctx, onNotAuthenticated) {
                  @Override
                  public void onResponse(
                        Call<List<EventDTO>> call, Response<List<EventDTO>> resp) {
                      super.onResponse(call, resp);
                      if (resp.code() != HttpCodes.OK) {
                          return;
                      }
                      events.setValue(resp.body());
                      return;
                  }
              });
    }

    @Override
    protected void onCleared() {
        logMethod(TAG, this);
        super.onCleared();
    }

    public LiveData<UserDTO> getUser() {
        return user;
    }

    public LiveData<List<EventDTO>> getEvents() {
        return events;
    }

    public LiveData<Boolean> getAmISubscriber() {
        return amISubscriber;
    }

    public LiveData<Bitmap> getPhoto() {
        return photo;
    }
}
