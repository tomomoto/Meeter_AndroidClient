package com.example.tom.meeter.infrastructure.eventbus.events;

/**
 * Created by Tom on 13.01.2017.
 */

public class SuccessfulLogin {

    private final String userId;

    public SuccessfulLogin(String id) {
        userId = id;
    }

    public String getUserId() {
        return userId;
    }
}
