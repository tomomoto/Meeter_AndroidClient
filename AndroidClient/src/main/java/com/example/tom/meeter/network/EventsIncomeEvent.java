package com.example.tom.meeter.network;

import org.json.JSONArray;

/**
 * Created by Tom on 14.01.2017.
 */

public class EventsIncomeEvent {

    private JSONArray events;

    public JSONArray getEvents() {
        return events;
    }

    public void setEvents(JSONArray events) {
        this.events = events;
    }

    public EventsIncomeEvent(JSONArray events) {
        this.events = events;
    }
}
