package com.example.tom.meeter.context.network.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tom on 14.01.2017.
 */

public class SearchForEvents implements NetworkEvent {

    private static String LATITUDE_KEY = "latitude";
    private static String LONGITUDE_KEY = "longitude";
    private static String DISTANCE_KEY = "distance";

    private float latitude;
    private float longitude;
    private int distance;

    public SearchForEvents(float latitude, float longitude, int distance) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "SearchForEvents{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", distance=" + distance +
                '}';
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put(LATITUDE_KEY, latitude)
                .put(LONGITUDE_KEY, longitude)
                .put(DISTANCE_KEY, distance);
    }
}
