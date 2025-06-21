package com.tom.meeter.infrastructure.binder;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.viewholder.EventViewHolder;

public interface EventBinder {

    void bind(EventViewHolder holder, EventDTO event);
}
