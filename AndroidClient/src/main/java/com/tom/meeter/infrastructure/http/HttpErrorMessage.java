package com.tom.meeter.infrastructure.http;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpErrorMessage {

    public final String code;

    @JsonProperty("localizable_message")
    public final LocalizableMessage localizableMessage;

    public HttpErrorMessage(
          String code, LocalizableMessage localizableMessage) {
        this.code = code;
        this.localizableMessage = localizableMessage;
    }

    public static HttpErrorMessage fromString(String str)
          throws JSONException {
        JSONObject json = new JSONObject(str);
        return new HttpErrorMessage(
              json.getString("code"),
              new LocalizableMessage(
                    json.getJSONObject("localizable_message")
                          .getString("message")));
    }

    public String getCode() {
        return code;
    }

    public LocalizableMessage getLocalizableMessage() {
        return localizableMessage;
    }

    public static class LocalizableMessage {
        private final String message;

        public LocalizableMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
