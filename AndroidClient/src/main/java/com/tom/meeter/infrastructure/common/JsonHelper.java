package com.tom.meeter.infrastructure.common;

import static com.tom.meeter.infrastructure.common.CommonHelper.UI_DATE_FORMAT;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.OffsetDateTime;

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

    @Nullable
    public static String getStringOrNull(
          String key, JSONObject json) throws JSONException {
        return json.isNull(key) ? null : json.getString(key);
    }

    @Nullable
    public static OffsetDateTime getOffsetDateTimeOrNull(
          String key, JSONObject json) throws JSONException {
        return json.isNull(key) ? null : OffsetDateTime.parse(json.getString(key));
    }

    @Nullable
    public static LocalDate getLocalDateOrNull(
          String key, JSONObject json) throws JSONException {
        return json.isNull(key) ? null : LocalDate.parse(json.getString(key), UI_DATE_FORMAT);
    }

    @Nullable
    public static Double getDoubleOrNull(
          String key, JSONObject json) throws JSONException {
        return json.isNull(key) ? null : json.getDouble(key);
    }

    @Nullable
    public static Float getFloatOrNull(
          String key, JSONObject json) throws JSONException {
        return json.isNull(key) ? null : (float) json.getDouble(key);
    }
}
