package com.tom.meeter.infrastructure.binder;

import com.tom.meeter.context.network.dto.EventDTO;

public interface EventBinder {

    void bind(EventViewHolder holder, EventDTO event);
}
