package com.tom.meeter.context.profile.component.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.profile.component.viewholder.EventViewHolder;
import com.tom.meeter.databinding.EventViewBinding;
import com.tom.meeter.infrastructure.components.adapter.BaseEventAdapter;
import com.tom.meeter.infrastructure.components.binder.EventBinder;

/**
 * created by Tom on 10.02.2017.
 */
public class ActiveEventsAdapter extends BaseEventAdapter<EventViewHolder> {

    private static final String TAG = ActiveEventsAdapter.class.getCanonicalName();

    public ActiveEventsAdapter(EventBinder<EventViewHolder> binder) {
        super(binder);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventViewHolder(
              EventViewBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
