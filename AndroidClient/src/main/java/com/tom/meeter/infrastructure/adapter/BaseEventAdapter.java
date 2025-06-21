package com.tom.meeter.infrastructure.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.binder.ViewHolderEventBinder;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEventAdapter<T extends RecyclerView.ViewHolder>
      extends RecyclerView.Adapter<T> {

    private final ViewHolderEventBinder<T> binder;
    private final List<EventDTO> events = new ArrayList<>();

    protected BaseEventAdapter(ViewHolderEventBinder<T> binder) {
        this.binder = binder;
    }

    public void setData(List<EventDTO> newEvents) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
              new EventsDiffCallback(events, newEvents));
        events.clear();
        events.addAll(newEvents);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {
        binder.bind(holder, events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
