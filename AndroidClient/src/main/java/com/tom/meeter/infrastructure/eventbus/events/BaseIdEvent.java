package com.tom.meeter.infrastructure.eventbus.events;

public class BaseIdEvent {
    private final String id;

    public BaseIdEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
