package com.example.tom.meeter.infrastructure.eventbus.events;

/**
 * Created by Tom on 13.01.2017.
 */
public class FailureLogin {

    private final String message;

    public FailureLogin(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
