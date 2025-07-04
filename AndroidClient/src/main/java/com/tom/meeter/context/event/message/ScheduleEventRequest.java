package com.tom.meeter.context.event.message;

import java.time.OffsetDateTime;
import java.util.Optional;

public class ScheduleEventRequest {

    private Optional<OffsetDateTime> starting;
    private Optional<OffsetDateTime> ending;

    public ScheduleEventRequest() {
    }

    public Optional<OffsetDateTime> getStarting() {
        return starting;
    }

    public Optional<OffsetDateTime> getEnding() {
        return ending;
    }

    public void setStarting(OffsetDateTime starting) {
        this.starting = Optional.ofNullable(starting);
    }

    public void setEnding(OffsetDateTime ending) {
        this.ending = Optional.ofNullable(ending);
    }

    public boolean isEmpty() {
        return starting == null && ending == null;
    }
}
