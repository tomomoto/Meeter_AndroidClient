package com.tom.meeter.infrastructure.components.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.tom.meeter.databinding.CardItemBinding;
import com.tom.meeter.infrastructure.components.binder.ViewHolderEventBinder;
import com.tom.meeter.infrastructure.components.viewholder.CardItemHolder;

public class EventsCardAdapter extends BaseEventAdapter<CardItemHolder> {

    private static final String TAG = EventsCardAdapter.class.getCanonicalName();

    public EventsCardAdapter(ViewHolderEventBinder<CardItemHolder> binder) {
        super(binder);
    }

    @NonNull
    @Override
    public CardItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardItemHolder(
              CardItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }
}
