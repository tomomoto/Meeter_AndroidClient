package com.example.tom.meeter.context.profile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tom.meeter.R;
import com.example.tom.meeter.context.network.EventDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Tom on 10.02.2017.
 */
public class RecycleViewActiveEventsAdapter extends RecyclerView.Adapter<EventViewHolder> {
    private final List<EventDTO> events = new ArrayList<>();

    public RecycleViewActiveEventsAdapter() {
    }

    public List<EventDTO> getEvents() {
        return events;
    }

    public void addEvent(EventDTO event) {
        events.add(event);
    }

    public void addEvents(List<EventDTO> events) {
        this.events.addAll(events);
    }

    public void cleanEvents() {
        events.clear();
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_view, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.getEventName().setText(events.get(position).getName());
        holder.getEventDescription().setText(events.get(position).getDescription());
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
