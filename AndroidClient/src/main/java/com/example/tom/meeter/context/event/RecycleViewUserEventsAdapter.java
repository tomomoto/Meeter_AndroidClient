package com.example.tom.meeter.context.event;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tom.meeter.R;
import com.example.tom.meeter.context.event.domain.Event;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * created by Tom on 10.02.2017.
 */

public class RecycleViewUserEventsAdapter extends RecyclerView.Adapter<RecycleViewUserEventsAdapter.EventViewHolder> {

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

    static class EventViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cv)
        CardView cardView;

        @BindView(R.id.event_name)
        TextView eventName;

        @BindView(R.id.event_description)
        TextView eventDescription;

        EventViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Event event) {
            if (event != null) {
                eventName.setText(event.getName());
                eventDescription.setText(event.getDescription());
                /*
                btnDelete.setOnClickListener(v -> {
                    if (onDeleteButtonClickListener != null)
                        onDeleteButtonClickListener.onDeleteButtonClicked(post);
                });*/
            }
        }
    }

    private List<Event> events;

    public RecycleViewUserEventsAdapter() {

    }

    public void setData(List<Event> newEvents) {
        if (events != null) {
            EventDiffCallback eventDiffCallback = new EventDiffCallback(events, newEvents);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(eventDiffCallback);
            events.clear();
            events.addAll(newEvents);
            diffResult.dispatchUpdatesTo(this);
        } else {
            events = newEvents;
        }

        //Do we need it?
        notifyDataSetChanged();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_view, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() {
        if (events == null) {
            return 0;
        }
        return events.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
