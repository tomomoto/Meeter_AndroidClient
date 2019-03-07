package com.example.tom.meeter.infrastructure;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * created by Tom on 10.02.2017.
 */

public class EventDTO {

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

    public static EventDTO encode(JSONObject event) throws JSONException {
        EventDTO result = new EventDTO();
        result.id = event.getInt("event_id");
        result.name = event.getString("name");
        result.description = event.getString("description");
        result.objectId = event.getInt("creator_id");
        result.latitude = event.getDouble("latitude");
        result.longitude = event.getDouble("longitude");
        result.created = event.getString("created");
        result.starting = event.getString("starting");
        result.ending = event.getString("ending");
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
