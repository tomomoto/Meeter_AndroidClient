package com.tom.meeter.infrastructure.components.binder;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.EventDTO;

public interface EventBinder<T extends RecyclerView.ViewHolder>
      extends BaseViewHolderBinder<T, EventDTO> {
    void bind(T holder, EventDTO event);
}
