package com.tom.meeter.context.profile.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.EventViewBinding;

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
        return new EventViewHolder(
              EventViewBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        EventDTO event = events.get(position);
        holder.bind(event.getName(), event.getDescription());
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
