package com.tom.meeter.context.profile.adapter;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.databinding.EventViewBinding;
import com.tom.meeter.infrastructure.adapter.BaseEventAdapter;
import com.tom.meeter.infrastructure.binder.ViewHolderEventBinder;
import com.tom.meeter.infrastructure.viewholder.EventViewHolder;

/**
 * created by Tom on 10.02.2017.
 */
public class EventsAdapter extends BaseEventAdapter<EventViewHolder> {

    private static final String TAG = EventsAdapter.class.getCanonicalName();

    public EventsAdapter(ViewHolderEventBinder<EventViewHolder> binder) {
        super(binder);
        logMethod(TAG, this);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        logMethod(TAG, this);
        return new EventViewHolder(
              EventViewBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        logMethod(TAG, this);
    }
}
