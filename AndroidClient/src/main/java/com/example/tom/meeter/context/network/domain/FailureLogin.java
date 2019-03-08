package com.example.tom.meeter.context.network.domain;

import org.json.JSONObject;

/**
 * Created by Tom on 13.01.2017.
 */
public class FailureLogin implements NetworkEvent {

    @Override
    public String toString() {
        return "FailureLogin{}";
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject();
    }
}
