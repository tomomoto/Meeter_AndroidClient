package com.tom.meeter.infrastructure.common;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonHelper {
    private JsonHelper() {
    }

    public static int getInt(JSONObject json, String key) {
        try {
            return json.getInt(key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getString(JSONObject json, String key) {
        try {
            return json.getString(key);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
