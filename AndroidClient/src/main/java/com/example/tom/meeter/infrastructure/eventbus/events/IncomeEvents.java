package com.example.tom.meeter.infrastructure.eventbus.events;

import org.json.JSONArray;

/**
 * Created by Tom on 14.01.2017.
 */

public class IncomeEvents {

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
    public String toString() {
        return events.toString();
    }
}
