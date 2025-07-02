package com.tom.meeter.context.event.viewmodel;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.context.event.utils.Utils.currentUserIsEventCreator;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.user.viewmodel.UserViewModel;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.Set;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import retrofit2.Call;
import retrofit2.Response;

public class EventViewModel extends ViewModel {

    private static final String TAG = UserViewModel.class.getCanonicalName();

    private final EventService eventService;
    private final ImageDownloader imageDownloader;
    private final String eventId;
    private final Context ctx;
    private final Runnable onNotAuthenticated;

    private final MutableLiveData<EventDTO> event = new MutableLiveData<>();
    private final MutableLiveData<Set<EventDTO.EventStatus>> transitions = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> eventPhoto = new MutableLiveData<>();

    @AssistedInject
    public EventViewModel(
          EventService eventService, ImageDownloader imageDownloader,
          @Assisted String eventId,
          @Assisted Context ctx,
          @Assisted Runnable onNotAuthenticated) {
        logMethod(TAG, this);
        this.eventService = eventService;
        this.imageDownloader = imageDownloader;
        this.eventId = eventId;
        this.ctx = ctx.getApplicationContext();
        this.onNotAuthenticated = onNotAuthenticated;
        init();
    }

    public void init() {
        AccountManager am = AccountManager.get(ctx);
        String auth = getAuthHeader(am);
        eventService.getEvent(auth, eventId).enqueue(
              //TODO check toast...
              new BaseOnNotAuthenticatedCallback<>(ctx, onNotAuthenticated) {
                  @Override
                  public void onResponse(Call<EventDTO> call, Response<EventDTO> resp) {
                      super.onResponse(call, resp);
                      EventDTO eventResp = resp.body();
                      if (resp.code() != HttpCodes.OK || eventResp == null) {
                          return;
                      }
                      event.postValue(eventResp);
                      if (currentUserIsEventCreator(am, eventResp)) {
                          fetchEventTransitions(auth);
                      }
                      String photoPath = eventResp.getPhotoPath();
                      if (photoPath == null) {
                          return;
                      }
                      imageDownloader.downloadEventImage(
                            photoPath, ctx, ImagesHelper::bigCircleImage,
                            eventPhoto::postValue, onNotAuthenticated);
                  }
              }
        );
    }

    private void fetchEventTransitions(String auth) {
        eventService.availableTransitions(auth, eventId).enqueue(
              new BaseOnNotAuthenticatedCallback<>(ctx, onNotAuthenticated) {
                  @Override
                  public void onResponse(
                        Call<Set<EventDTO.EventStatus>> call,
                        Response<Set<EventDTO.EventStatus>> resp) {
                      super.onResponse(call, resp);
                      Set<EventDTO.EventStatus> eventStatuses = resp.body();
                      if (resp.code() != HttpCodes.OK || eventStatuses == null) {
                          return;
                      }
                      transitions.postValue(eventStatuses);
                  }
              });
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

    public LiveData<Set<EventDTO.EventStatus>> getTransitions() {
        return transitions;
    }
}
