package com.tom.meeter.infrastructure.components.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.tom.meeter.databinding.CardItemBinding;
import com.tom.meeter.infrastructure.components.binder.EventBinder;
import com.tom.meeter.infrastructure.components.binder.SimpleEventBinderImpl;
import com.tom.meeter.infrastructure.components.viewholder.CardItemHolder;

import javax.inject.Inject;

public class EventsCardAdapter extends BaseEventAdapter<CardItemHolder> {

    private static final String TAG = EventsCardAdapter.class.getCanonicalName();

    private final EventBinder<CardItemHolder> binder;

    @Inject
    public EventsCardAdapter(SimpleEventBinderImpl binder) {
        super(binder);
        this.binder = binder;
    }

    public void initialize(
          Context ctx, Runnable onAuthFail,
          OnEventClickListener listener) {
        binder.setContext(ctx);
        binder.setOnAuthFailAction(onAuthFail);
        binder.setupOnEventClickListener(listener);
    }

    @NonNull
    @Override
    public CardItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardItemHolder(
              CardItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }
}
