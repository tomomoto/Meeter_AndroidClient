package com.tom.meeter.context.profile.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class CreateEventRequest {

    private final String PHOTO_PATH_KEY = "photo_path";

    private final String name;
    private final String description;
    private final OffsetDateTime starting;
    private final OffsetDateTime ending;
    private final String city;
    private final Double latitude;
    private final Double longitude;
    @JsonProperty(value = PHOTO_PATH_KEY)
    private final String photoPath;

    public CreateEventRequest(
          String name, String description, OffsetDateTime starting,
          OffsetDateTime ending, String city, Double latitude,
          Double longitude, String photoPath) {
        this.name = name;
        this.description = description;
        this.starting = starting;
        this.ending = ending;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoPath = photoPath;
    }

    public boolean isEmpty() {
        return name == null && description == null
              && starting == null && ending == null
              && city == null && latitude == null
              && longitude == null && photoPath == null;
    }
}
