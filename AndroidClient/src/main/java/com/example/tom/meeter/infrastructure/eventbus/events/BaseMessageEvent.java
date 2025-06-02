package com.example.tom.meeter.infrastructure.eventbus.events;

public class BaseMessageEvent {
    private final String message;

    public BaseMessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
