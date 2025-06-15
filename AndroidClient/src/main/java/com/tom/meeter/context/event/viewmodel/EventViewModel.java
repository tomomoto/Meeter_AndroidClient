package com.tom.meeter.context.event.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.event.service.EventService;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.user.viewmodel.UserViewModel;
import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.DisconnectLogger;
import com.tom.meeter.infrastructure.http.HttpCodes;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class EventViewModel extends ViewModel {

    private static final String TAG = UserViewModel.class.getCanonicalName();

    private final MutableLiveData<EventDTO> eventLiveData = new MutableLiveData<>();

    private final EventService eventService;

    @Inject
    public EventViewModel(EventService eventService) {
        logMethod(TAG, this);
        this.eventService = eventService;
    }

    public void fetchEventInformation(String token, String eventId, Activity activity) {
        eventService.getEvent(Globals.getAuthHeader(token), eventId).enqueue(
              new DisconnectLogger<>(activity) {
                  @Override
                  public void onResponse(Call<EventDTO> call, Response<EventDTO> response) {
                      if (response.code() == HttpCodes.OK && response.body() != null) {
                          eventLiveData.setValue(response.body());
                          return;
                      }
                      if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                          activity.recreate();
                      }
                      Log.i(TAG, "/event/{id}: " + response.code() + " : " + response.body());
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
}

