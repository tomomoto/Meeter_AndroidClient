package com.tom.meeter.context.event.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.user.viewmodel.UserViewModel;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.http.HttpCodes;
import com.tom.meeter.infrastructure.http.HttpErrorLogger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import retrofit2.Call;
import retrofit2.Response;

public class EventViewModel extends ViewModel {

    private static final String TAG = UserViewModel.class.getCanonicalName();
    private static final String ASSISTED_EVENT = "event_id";
    private static final String ASSISTED_TOKEN = "token";
    private static final String ASSISTED_ON_NOT_AUTHENTICATED = "on_not_authenticated";
    private static final String ASSISTED_ON_AUTH_FAIL = "on_auth_fail";

    private final EventService eventService;
    private final ImageDownloader imageDownloader;
    private final String eventId;
    private final String token;
    private final Context ctx;
    private final Runnable onNotAuthenticated;
    private final Runnable onAuthFail;


    private final MutableLiveData<EventDTO> event = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> eventPhoto = new MutableLiveData<>();

    @AssistedInject
    public EventViewModel(
          EventService eventService, ImageDownloader imageDownloader,
          @Assisted(ASSISTED_EVENT) String eventId,
          @Assisted(ASSISTED_TOKEN) String token,
          @Assisted Context ctx,
          @Assisted(ASSISTED_ON_NOT_AUTHENTICATED) Runnable onNotAuthenticated,
          @Assisted(ASSISTED_ON_AUTH_FAIL) Runnable onAuthFail) {
        logMethod(TAG, this);
        this.eventService = eventService;
        this.imageDownloader = imageDownloader;
        this.eventId = eventId;
        this.token = token;
        this.ctx = ctx.getApplicationContext();
        this.onNotAuthenticated = onNotAuthenticated;
        this.onAuthFail = onAuthFail;
        init();
    }

    @AssistedFactory
    public interface EventViewModelAssistedFactory {
        EventViewModel create(
              @Assisted(ASSISTED_EVENT) String eventId,
              @Assisted(ASSISTED_TOKEN) String token,
              @Assisted Context ctx,
              @Assisted(ASSISTED_ON_NOT_AUTHENTICATED) Runnable onNotAuthenticated,
              @Assisted(ASSISTED_ON_AUTH_FAIL) Runnable onAuthFail);
    }

    public static ViewModelProvider.Factory providesFactory(
          EventViewModelAssistedFactory assistedFactory,
          String eventId, String token, Context ctx,
          Runnable onNotAuthenticated, Runnable onAuthFail) {
        return new ViewModelProvider.Factory() {
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) assistedFactory.create(
                      eventId, token, ctx, onNotAuthenticated, onAuthFail);
            }
        };
    }

    public void init() {
        eventService.getEvent(Globals.getAuthHeader(token), eventId).enqueue(
              //TODO check toast...
              new HttpErrorLogger<>(ctx) {
                  @Override
                  public void onResponse(Call<EventDTO> call, Response<EventDTO> resp) {
                      super.onResponse(call, resp);
                      EventDTO body = resp.body();
                      if (resp.code() == HttpCodes.OK && body != null) {
                          event.setValue(body);
                          String photoPath = body.getPhotoPath();
                          if (photoPath != null) {
                              imageDownloader.downloadEventImage(
                                    photoPath, ctx, eventPhoto::setValue,
                                    ImagesHelper::bigCircleImage, onAuthFail);
                          }
                          return;
                      }
                      if (resp.code() == HttpCodes.NOT_AUTHENTICATED) {
                          onNotAuthenticated.run();
                      }
                  }
              }
        );
    }

    @Override
    protected void onCleared() {
        logMethod(TAG, this);
        super.onCleared();
    }

    public LiveData<EventDTO> getEvent() {
        return event;
    }

    public LiveData<Bitmap> getEventPhoto() {
        return eventPhoto;
    }
}
