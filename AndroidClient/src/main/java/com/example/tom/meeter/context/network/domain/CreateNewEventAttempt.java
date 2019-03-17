package com.example.tom.meeter.context.network.domain;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateNewEventAttempt implements NetworkEvent {

  private String name;
  private String description;
  private String starting;
  private String ending;
  private Double latitude;
  private Double longitude;

  public CreateNewEventAttempt(String name, String description, String starting, String ending, Double latitude,
      Double longitude) {
    this.name = name;
    this.description = description;
    this.starting = starting;
    this.ending = ending;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  @Override
  public JSONObject toJson() throws JSONException {
    return new JSONObject()
        .put("name", name)
        .put("description", description)
        .put("starting", starting)
        .put("ending", ending)
        .put("latitude", latitude)
        .put("longitude", longitude);
  }
}
