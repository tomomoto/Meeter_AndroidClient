package com.tom.meeter.context.network.domain;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tom on 14.01.2017.
 */

public record SearchForEvents(double latitude, double longitude, int distance)
      implements NetworkEvent {

    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String DISTANCE_KEY = "distance";

    @NonNull
    @Override
    public String toString() {
        return "SearchForEvents{" +
              "latitude=" + latitude +
              ", longitude=" + longitude +
              ", distance=" + distance +
              '}';
    }

    @Override
    public JSONObject toJson() {
        try {
            return new JSONObject()
                  .put(LATITUDE_KEY, latitude)
                  .put(LONGITUDE_KEY, longitude)
                  .put(DISTANCE_KEY, distance);
        } catch (JSONException e) {
            throw new RuntimeException("Unable to call SearchForEvents.toJson(): ", e);
        }
    }
}
