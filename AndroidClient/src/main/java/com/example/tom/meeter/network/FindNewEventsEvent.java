package com.example.tom.meeter.network;

/**
 * Created by Tom on 14.01.2017.
 */

public class FindNewEventsEvent {
    private double Latitude;
    private double Longitude;
    private double Area;

    public FindNewEventsEvent(double latitude, double longitude, double area) {
        Latitude = latitude;
        Longitude = longitude;
        Area = area;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getArea() {
        return Area;
    }

    public void setArea(double area) {
        Area = area;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }
}
