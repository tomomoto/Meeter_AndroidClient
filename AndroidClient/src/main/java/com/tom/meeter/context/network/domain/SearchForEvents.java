package com.tom.meeter.context.network.domain;

import com.tom.meeter.context.network.dto.EventDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

/**
 * Created by Tom on 14.01.2017.
 */

public record SearchForEvents(
      double latitude, double longitude, int distance,
      Set<EventDTO.EventStatus> status)
      implements NetworkEvent {

    private static final String LATITUDE_KEY = "latitude";
    private static final String LONGITUDE_KEY = "longitude";
    private static final String DISTANCE_KEY = "distance";
    private static final String STATUS_KEY = "status";

    @Override
    public String toString() {
        return "SearchForEvents{" +
              "latitude=" + latitude +
              ", longitude=" + longitude +
              ", distance=" + distance +
              ", status=" + status +
              '}';
    }

    @Override
    public JSONObject toJson() {
        try {
            JSONObject result = new JSONObject()
                  .put(LATITUDE_KEY, latitude)
                  .put(LONGITUDE_KEY, longitude)
                  .put(DISTANCE_KEY, distance);
            result.put(STATUS_KEY, new JSONArray(EventDTO.EventStatus.transformToStrings(status)));
            return result;
        } catch (JSONException e) {
            throw new RuntimeException("Unable to encode SearchForEvents to json: ", e);
        }
    }
}
