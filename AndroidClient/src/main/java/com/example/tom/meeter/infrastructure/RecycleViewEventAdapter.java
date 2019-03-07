package com.example.tom.meeter.infrastructure;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tom.meeter.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * created by Tom on 10.02.2017.
 */

public class RecycleViewEventAdapter extends RecyclerView.Adapter<RecycleViewEventAdapter.EventViewHolder> {

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
    }

    private List<EventDTO> events;

    public RecycleViewEventAdapter(List<EventDTO> events) {
        this.events = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_view, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.eventName.setText(events.get(position).getName());
        holder.eventDescription.setText(events.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
