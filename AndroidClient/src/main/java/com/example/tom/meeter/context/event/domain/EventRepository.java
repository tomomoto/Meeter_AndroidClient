package com.example.tom.meeter.context.event.domain;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.tom.meeter.context.network.EventDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class EventRepository {

    private static final String TAG = EventRepository.class.getCanonicalName();

    private final EventService eventService;
    private final EventDao eventDao;
    private final Executor executor;

    @Inject
    public EventRepository(EventService eventService, EventDao eventDao, Executor executor) {
        this.eventService = eventService;
        this.eventDao = eventDao;
        this.executor = executor;
    }

    public LiveData<List<Event>> getUserEvents(String userId) {
        refreshUserEvents(userId);
        return eventDao.loadLDByCreatorId(userId);
    }

    private void refreshUserEvents(final String userId) {
        executor.execute(() -> {
            Response<List<EventDTO>> execute = null;
            try {
                execute = eventService.getEventsByCreatorId(userId).execute();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            if (execute != null) {
                List<EventDTO> body = execute.body();
                if (body != null) {
                    List<Event> result = new ArrayList<>();
                    for (EventDTO i : body) {
                        result.add(new Event(i.getId(), i.getName(), i.getDescription(), i.getLatitude(),
                                i.getLongitude(), i.getCreator_id(), i.getCreated(), i.getStarting(),
                                i.getEnding()));
                    }
                    eventDao.saveAll(result);
                }
            }
        });
    }

}