package com.example.tom.meeter.context.network.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tom on 14.01.2017.
 */

public class FindNewEventsEvent {

    private static String LATITUDE_KEY = "latitude";
    private static String LONGITUDE_KEY = "longitude";
    private static String AREA_KEY = "area";

    private double latitude;
    private double longitude;
    private double area;

    public FindNewEventsEvent(double latitude, double longitude, double area) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.area = area;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "FindNewEventsEvent{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", area=" + area +
                '}';
    }

    public JSONObject jsonize() throws JSONException {
        return new JSONObject()
                .put(LATITUDE_KEY, latitude)
                .put(LONGITUDE_KEY, longitude)
                .put(AREA_KEY, area);
    }
}
