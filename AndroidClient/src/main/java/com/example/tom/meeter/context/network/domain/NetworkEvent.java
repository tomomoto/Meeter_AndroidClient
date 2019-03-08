package com.example.tom.meeter.context.network.domain;

import org.json.JSONException;
import org.json.JSONObject;

public interface NetworkEvent {

    JSONObject toJson() throws JSONException;
}
