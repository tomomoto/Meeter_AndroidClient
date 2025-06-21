package com.tom.meeter.infrastructure.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.databinding.CardItemBinding;
import com.tom.meeter.infrastructure.binder.CardBinder;
import com.tom.meeter.infrastructure.viewholder.CardItemHolder;

import java.util.ArrayList;
import java.util.List;

public class EventsCardAdapter extends RecyclerView.Adapter<CardItemHolder> {

    private static final String TAG = EventsCardAdapter.class.getCanonicalName();

    private final CardBinder binder;
    private final List<EventDTO> events = new ArrayList<>();

    public EventsCardAdapter(CardBinder binder) {
        this.binder = binder;
    }

    public void setData(List<EventDTO> newEvents) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
              new EventsDiffCallback(events, newEvents));
        events.clear();
        events.addAll(newEvents);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public CardItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardItemHolder(
              CardItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CardItemHolder holder, int position) {
        binder.bind(holder, events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
