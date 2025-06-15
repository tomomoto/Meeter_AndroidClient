package com.tom.meeter.infrastructure.eventbus.events;

import androidx.annotation.NonNull;

import com.tom.meeter.context.network.dto.EventDTO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 14.01.2017.
 */
public record IncomeEvents(List<EventDTO> events) {
    public static IncomeEvents fromJsonArray(JSONArray msg) {
        List<EventDTO> events = new ArrayList<>();
        for (int i = 0; i < msg.length(); i++) {
            events.add(EventDTO.encode((JSONObject) msg.opt(i)));
        }
        return new IncomeEvents(events);
    }

    @NonNull
    public String toString() {
        return events.toString();
    }
}
