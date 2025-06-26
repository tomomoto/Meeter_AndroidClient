package com.tom.meeter.infrastructure.components.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.components.binder.EventBinder;

public abstract class BaseEventAdapter<T extends RecyclerView.ViewHolder>
      extends BaseAdapter<T, EventDTO> {

    public BaseEventAdapter(EventBinder<T> binder) {
        super(binder);
    }
}
