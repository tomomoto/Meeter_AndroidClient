package com.tom.meeter.infrastructure.eventbus.events;

import com.tom.meeter.context.network.dto.EventDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 14.01.2017.
 */

public class IncomeEvents {

    private final List<EventDTO> events = new ArrayList<>();

    public List<EventDTO> getEvents() {
        return events;
    }

    public IncomeEvents(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                events.add(EventDTO.encode((JSONObject) jsonArray.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return events.toString();
    }
}
