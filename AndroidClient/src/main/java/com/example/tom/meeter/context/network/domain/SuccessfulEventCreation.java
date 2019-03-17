package com.example.tom.meeter.context.network.domain;

public class SuccessfulEventCreation {

  private String eventId;

  public SuccessfulEventCreation(String eventId) {
    this.eventId = eventId;
  }

  public String getEventId() {
    return eventId;
  }
}
