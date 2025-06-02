package com.example.tom.meeter.infrastructure.eventbus.events;

/**
 * Created by Tom on 13.01.2017.
 */
public class FailureLogin extends BaseMessageEvent {

    public FailureLogin(String message) {
        super(message);
    }
}
