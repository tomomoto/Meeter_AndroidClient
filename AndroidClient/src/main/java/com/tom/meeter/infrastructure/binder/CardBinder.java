package com.tom.meeter.infrastructure.binder;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.viewholder.CardItemHolder;

public interface CardBinder {

    void bind(CardItemHolder holder, EventDTO event);
}
