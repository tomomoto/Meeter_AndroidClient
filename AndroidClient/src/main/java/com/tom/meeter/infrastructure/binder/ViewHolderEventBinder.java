package com.tom.meeter.infrastructure.binder;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.EventDTO;

public interface ViewHolderEventBinder<T extends RecyclerView.ViewHolder> {

    void bind(T holder, EventDTO event);
}
