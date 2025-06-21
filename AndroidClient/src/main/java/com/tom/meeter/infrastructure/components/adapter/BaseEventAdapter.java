package com.tom.meeter.infrastructure.components.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.components.binder.ViewHolderEventBinder;

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
        this.events.clear();
        this.events.addAll(newEvents);
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

    static class EventsDiffCallback extends DiffUtil.Callback {

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
}
