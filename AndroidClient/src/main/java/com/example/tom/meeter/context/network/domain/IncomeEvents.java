package com.example.tom.meeter.context.network.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tom on 14.01.2017.
 */

public class IncomeEvents implements NetworkEvent{

    private JSONArray events;

    public JSONArray getEvents() {
        return events;
    }

    public void setEvents(JSONArray events) {
        this.events = events;
    }

    public IncomeEvents(JSONArray events) {
        this.events = events;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject().put("events", events);
    }

    @Override
    public String toString() {
        return events.toString();
    }
}
