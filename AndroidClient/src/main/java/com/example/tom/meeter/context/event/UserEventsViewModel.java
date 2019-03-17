package com.example.tom.meeter.context.event;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.tom.meeter.context.event.domain.Event;
import com.example.tom.meeter.context.event.domain.EventRepository;

import java.util.List;

import javax.inject.Inject;

public class UserEventsViewModel extends ViewModel {

  private String userId;
  private LiveData<List<Event>> userEvents;

  private EventRepository eventRepository;

  @Inject
  public UserEventsViewModel(EventRepository eventRepository) {
    this.eventRepository = eventRepository;
  }

  public void init(String userId) {
    this.userId = userId;
    if (userEvents != null) {
      return;
    }
    userEvents = eventRepository.getUserEvents(userId);
  }

  public String getUserId() {
    return userId;
  }

  public LiveData<List<Event>> getUserEvents() {
    return userEvents;
  }
}
