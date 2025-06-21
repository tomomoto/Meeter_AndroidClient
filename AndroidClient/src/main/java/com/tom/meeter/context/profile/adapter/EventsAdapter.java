package com.tom.meeter.context.profile.adapter;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.EventViewBinding;
import com.tom.meeter.infrastructure.binder.EventBinder;
import com.tom.meeter.infrastructure.binder.EventViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * created by Tom on 10.02.2017.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private static final String TAG = EventsAdapter.class.getCanonicalName();

    private final List<EventDTO> events = new ArrayList<>();

    private final EventBinder eventBinder;

    public EventsAdapter(EventBinder eventBinder) {
        logMethod(TAG, this);
        this.eventBinder = eventBinder;
    }

    public void setData(List<EventDTO> newEvents) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
              new EventsDiffCallback(events, newEvents));
        if (!events.isEmpty()) {
            events.clear();
        }
        events.addAll(newEvents);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        logMethod(TAG, this);
        return new EventViewHolder(
              EventViewBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        logMethod(TAG, this);
        eventBinder.bind(holder, events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        logMethod(TAG, this);
    }
}
