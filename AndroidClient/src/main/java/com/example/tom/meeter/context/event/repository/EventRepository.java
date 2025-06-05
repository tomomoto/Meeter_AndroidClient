package com.example.tom.meeter.context.event.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.tom.meeter.context.event.database.EventDao;
import com.example.tom.meeter.context.event.domain.Event;
import com.example.tom.meeter.context.event.service.EventService;
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

    public LiveData<List<Event>> getUserEventsLiveData(String userId) {
        refreshUserEvents(userId);
        return eventDao.loadLDByCreatorId(userId);
    }

    private void refreshUserEvents(String userId) {
        executor.execute(
            () -> {
                Response<List<EventDTO>> execute = null;
                try {
                    execute = eventService.getEventsByCreatorId(userId).execute();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                if (execute == null) {
                    return;
                }
                List<EventDTO> body = execute.body();
                if (body == null) {
                    return;
                }
                List<Event> result = new ArrayList<>();
                body.stream()
                    .forEach(i -> result.add(
                        new Event(i.getId(), i.getName(), i.getDescription(), i.getLatitude(),
                            i.getLongitude(), i.getCreator_id(), i.getCreated(), i.getStarting(),
                            i.getEnding())
                    ));
                eventDao.deleteByUserId(userId);
                eventDao.saveAll(result);
            });
    }
}
