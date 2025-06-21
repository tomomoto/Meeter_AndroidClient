package com.tom.meeter.infrastructure.adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.tom.meeter.context.network.dto.EventDTO;

import java.util.List;

public class EventsDiffCallback extends DiffUtil.Callback {

    private final List<EventDTO> oldEvents, newEvents;

    public EventsDiffCallback(List<EventDTO> oldEvents, List<EventDTO> newEvents) {
        this.oldEvents = oldEvents;
        this.newEvents = newEvents;
    }

    @Override
    public int getOldListSize() {
        return oldEvents.size();
    }

    @Override
    public int getNewListSize() {
        return newEvents.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldEvents.get(oldItemPosition).getId()
              .equals(newEvents.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldEvents.get(oldItemPosition)
              .equals(newEvents.get(newItemPosition));
    }
}