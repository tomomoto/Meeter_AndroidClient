package com.tom.meeter.context.profile.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.EventViewBinding;
import com.tom.meeter.infrastructure.binder.EventBinder;
import com.tom.meeter.infrastructure.viewholder.EventViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Tom on 10.02.2017.
 */
public class ActiveEventsAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private static final String TAG = ActiveEventsAdapter.class.getCanonicalName();

    private final List<EventDTO> events = new ArrayList<>();

    private final EventBinder eventBinder;

    public ActiveEventsAdapter(EventBinder eventBinder) {
        this.eventBinder = eventBinder;
    }

    public List<EventDTO> getEvents() {
        return events;
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
        eventBinder.bind(holder, events.get(position));
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
