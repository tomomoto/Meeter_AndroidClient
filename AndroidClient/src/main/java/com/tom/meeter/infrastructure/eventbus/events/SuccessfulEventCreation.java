package com.tom.meeter.infrastructure.eventbus.events;

public class SuccessfulEventCreation extends BaseIdEvent {

    public SuccessfulEventCreation(String id) {
        super(id);
    }
}
