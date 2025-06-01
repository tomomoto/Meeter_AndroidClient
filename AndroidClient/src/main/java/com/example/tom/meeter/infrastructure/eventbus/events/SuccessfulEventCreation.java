package com.example.tom.meeter.infrastructure.eventbus.events;

public class SuccessfulEventCreation {

  private String eventId;

  public SuccessfulEventCreation(String eventId) {
    this.eventId = eventId;
  }

  public String getEventId() {
    return eventId;
  }
}
