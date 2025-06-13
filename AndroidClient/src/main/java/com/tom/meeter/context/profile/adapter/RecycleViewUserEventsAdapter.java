package com.tom.meeter.context.profile.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.profile.event.domain.Event;
import com.tom.meeter.databinding.EventViewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Tom on 10.02.2017.
 */

public class RecycleViewUserEventsAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private final List<Event> events = new ArrayList<>();

    public RecycleViewUserEventsAdapter() {
    }

    public void setData(List<Event> events) {
        EventDiffCallback eventDiffCallback = new EventDiffCallback(this.events, events);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(eventDiffCallback);
        this.events.clear();
        this.events.addAll(events);
        diffResult.dispatchUpdatesTo(this);
        //Do we need it?
        notifyDataSetChanged();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventViewHolder(
              EventViewBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event.getName(), event.getDescription());
        /*
        btnDelete.setOnClickListener(v -> {
            if (onDeleteButtonClickListener != null)
                onDeleteButtonClickListener.onDeleteButtonClicked(post);
        });*/
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private static class EventDiffCallback extends DiffUtil.Callback {

        private final List<Event> oldPosts, newPosts;

        EventDiffCallback(List<Event> oldPosts, List<Event> newPosts) {
            this.oldPosts = oldPosts;
            this.newPosts = newPosts;
        }

        @Override
        public int getOldListSize() {
            return oldPosts.size();
        }

        @Override
        public int getNewListSize() {
            return newPosts.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).getId().equals(newPosts.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }

}
