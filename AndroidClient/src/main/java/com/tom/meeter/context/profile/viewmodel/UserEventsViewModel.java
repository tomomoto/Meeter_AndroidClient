package com.tom.meeter.context.profile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.profile.repository.event.domain.Event;
import com.tom.meeter.context.profile.repository.event.repository.EventRepository;

import java.util.List;

@Deprecated
public class UserEventsViewModel extends ViewModel {

    private String userId;
    private LiveData<List<Event>> userEvents;

    private final EventRepository eventRepository;

    //@Inject
    public UserEventsViewModel(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void init(String userId) {
        this.userId = userId;
        if (userEvents == null) {
            userEvents = eventRepository.getUserEventsLiveData(userId);
        }
    }

    public String getUserId() {
        return userId;
    }

    public LiveData<List<Event>> getUserEvents() {
        return userEvents;
    }
}
