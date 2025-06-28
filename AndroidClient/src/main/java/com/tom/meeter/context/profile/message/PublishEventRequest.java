package com.tom.meeter.context.profile.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public class PublishEventRequest {

    private final String PHOTO_PATH_KEY = "photo_path";

    private String name;
    private String description;
    private OffsetDateTime starting;
    private OffsetDateTime ending;
    private String city;
    private Double latitude;
    private Double longitude;
    @JsonProperty(value = PHOTO_PATH_KEY)
    private String photoPath;

    public PublishEventRequest() {
    }

    public PublishEventRequest(
          String name, String description, OffsetDateTime starting, OffsetDateTime ending,
          String city, Double latitude, Double longitude, String photoPath) {
        this.name = name;
        this.description = description;
        this.starting = starting;
        this.ending = ending;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photoPath = photoPath;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getStarting() {
        return starting;
    }

    public OffsetDateTime getEnding() {
        return ending;
    }

    public String getCity() {
        return city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStarting(OffsetDateTime starting) {
        this.starting = starting;
    }

    public void setEnding(OffsetDateTime ending) {
        this.ending = ending;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean isEmpty() {
        return name == null && description == null && starting == null && ending == null
              && city == null && latitude == null && longitude == null && photoPath == null;
    }
}
