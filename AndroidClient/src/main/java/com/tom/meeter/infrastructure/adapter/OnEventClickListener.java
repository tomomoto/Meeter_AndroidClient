package com.tom.meeter.infrastructure.adapter;

import com.tom.meeter.context.network.dto.EventDTO;

public interface OnEventClickListener {
    void onEventClick(EventDTO event);
}
