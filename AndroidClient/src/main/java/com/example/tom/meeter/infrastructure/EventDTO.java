package com.example.tom.meeter.infrastructure;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * created by Tom on 10.02.2017.
 */

public class EventDTO {

    private static String EVENT_ID_KEY = "event_id";
    private static String NAME_KEY = "name";
    private static String DESCRIPTION_KEY = "description";
    private static String CREATOR_ID_KEY = "creator_id";
    private static String LATITUDE_KEY = "latitude";
    private static String LONGITUDE_KEY = "longitude";
    private static String CREATED_KEY = "created";
    private static String STARTING_KEY = "starting";
    private static String ENDING_KEY = "ending";

    private Integer id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private Integer objectId;
    private String created;
    private String starting;
    private String ending;

    public EventDTO() {
    }

    public static EventDTO encode(JSONObject json) throws JSONException {
        EventDTO result = new EventDTO();
        result.id = json.getInt(EVENT_ID_KEY);
        result.name = json.getString(NAME_KEY);
        result.description = json.getString(DESCRIPTION_KEY);
        result.objectId = json.getInt(CREATOR_ID_KEY);
        result.latitude = json.getDouble(LATITUDE_KEY);
        result.longitude = json.getDouble(LONGITUDE_KEY);
        result.created = json.getString(CREATED_KEY);
        result.starting = json.getString(STARTING_KEY);
        result.ending = json.getString(ENDING_KEY);
        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
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
}
