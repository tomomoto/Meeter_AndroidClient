package com.tom.meeter.context.network.dto;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * created by Tom on 10.02.2017.
 */

public class EventDTO {

    private static final String EVENT_ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String DESCRIPTION_KEY = "description";
    private static final String CREATOR_ID_KEY = "creator_id";
    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String CREATED_KEY = "created";
    private static final String STARTING_KEY = "starting";
    private static final String ENDING_KEY = "ending";

    private String id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    @SerializedName(value = CREATOR_ID_KEY)
    private String creatorId;
    private String created;
    private String starting;
    private String ending;

    public EventDTO() {
    }

    public static EventDTO encode(JSONObject json) {
        EventDTO result = new EventDTO();
        try {
            result.id = json.getString(EVENT_ID_KEY);
            result.name = json.getString(NAME_KEY);
            result.description = json.getString(DESCRIPTION_KEY);
            result.creatorId = json.getString(CREATOR_ID_KEY);
            result.latitude = json.getDouble(LATITUDE_KEY);
            result.longitude = json.getDouble(LONGITUDE_KEY);
            result.created = json.getString(CREATED_KEY);
            result.starting = json.getString(STARTING_KEY);
            result.ending = json.getString(ENDING_KEY);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to encode EventDTO from jsonObject, ", e);
        }
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getStarting() {
        return starting;
    }

    public void setStarting(String starting) {
        this.starting = starting;
    }

    public String getEnding() {
        return ending;
    }

    public void setEnding(String ending) {
        this.ending = ending;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EventDTO eventDTO)) {
            return false;
        }
        return Double.compare(latitude, eventDTO.latitude) == 0
              && Double.compare(longitude, eventDTO.longitude) == 0
              && Objects.equals(id, eventDTO.id)
              && Objects.equals(name, eventDTO.name)
              && Objects.equals(description, eventDTO.description)
              && Objects.equals(creatorId, eventDTO.creatorId)
              && Objects.equals(created, eventDTO.created)
              && Objects.equals(starting, eventDTO.starting)
              && Objects.equals(ending, eventDTO.ending);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, latitude, longitude,
              creatorId, created, starting, ending);
    }
}
