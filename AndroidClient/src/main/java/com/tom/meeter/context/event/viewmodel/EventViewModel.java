package com.tom.meeter.context.event.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.user.viewmodel.UserViewModel;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.ErrorLogger;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class EventViewModel extends ViewModel {

    private static final String TAG = UserViewModel.class.getCanonicalName();

    private final MutableLiveData<EventDTO> eventLiveData = new MutableLiveData<>();
    private final MutableLiveData<ResponseBody> eventPhotoLiveData = new MutableLiveData<>();

    private final EventService eventService;
    private final ImageDownloader imageDownloader;

    @Inject
    public EventViewModel(EventService eventService, ImageDownloader imageDownloader) {
        logMethod(TAG, this);
        this.eventService = eventService;
        this.imageDownloader = imageDownloader;
    }

    public void fetchEventInformation(String token, String eventId, Activity activity) {
        eventService.getEvent(Globals.getAuthHeader(token), eventId).enqueue(
              new ErrorLogger<>(activity) {
                  @Override
                  public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                      EventDTO body = response.body();
                      if (response.code() == HttpCodes.OK && body != null) {
                          eventLiveData.setValue(body);
                          String photoPath = body.getPhotoPath();
                          if (photoPath != null) {
                              imageDownloader.downloadEventImage(
                                    photoPath,
                                    activity.getApplicationContext(),
                                    eventPhotoLiveData::setValue,
                                    activity::recreate);
                          }
                          return;
                      }
                      if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                          activity.recreate();
                      }
                      Log.i(TAG, "/event/{id}: " + response.code() + " : " + body);
                  }
              }
        );
    }

    @Override
    protected void onCleared() {
        logMethod(TAG, this);
        super.onCleared();
    }

    public LiveData<EventDTO> getEventLiveData() {
        return eventLiveData;
    }

    public LiveData<ResponseBody> getEventPhotoLiveData() {
        return eventPhotoLiveData;
    }
}

