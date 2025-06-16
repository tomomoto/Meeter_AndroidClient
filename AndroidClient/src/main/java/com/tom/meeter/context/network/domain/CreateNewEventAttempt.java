package com.tom.meeter.context.network.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.OffsetDateTime;

public class CreateNewEventAttempt implements NetworkEvent {

    private final String name;
    private final String description;
    private final OffsetDateTime starting;
    private final OffsetDateTime ending;
    private final Float latitude;
    private final Float longitude;

    public CreateNewEventAttempt(
          String name, String description, OffsetDateTime starting,
          OffsetDateTime ending, Float latitude, Float longitude) {
        this.name = name;
        this.description = description;
        this.starting = starting;
        this.ending = ending;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public JSONObject toJson() {
        try {
            return new JSONObject()
                  .put("name", name)
                  .put("description", description)
                  .put("starting", starting)
                  .put("ending", ending)
                  .put("latitude", latitude)
                  .put("longitude", longitude);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to call CreateNewEventAttempt.toJson(): ", e);
        }
    }

    @Override
    public String toString() {
        return "CreateNewEventAttempt{" +
              "name='" + name +
              ", description='" + description +
              ", starting=" + starting +
              ", ending=" + ending +
              ", latitude=" + latitude +
              ", longitude=" + longitude +
              '}';
    }
}
