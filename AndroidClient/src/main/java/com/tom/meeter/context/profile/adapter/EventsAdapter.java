package com.tom.meeter.context.profile.adapter;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.databinding.EventViewBinding;
import com.tom.meeter.infrastructure.components.adapter.BaseEventAdapter;
import com.tom.meeter.infrastructure.components.adapter.OnEventClickListener;
import com.tom.meeter.infrastructure.components.binder.EventBinder;
import com.tom.meeter.infrastructure.components.binder.EventBinderImpl;
import com.tom.meeter.infrastructure.components.viewholder.EventViewHolder;

import javax.inject.Inject;

/**
 * created by Tom on 10.02.2017.
 */
public class EventsAdapter extends BaseEventAdapter<EventViewHolder> {

    private static final String TAG = EventsAdapter.class.getCanonicalName();

    private final EventBinder<EventViewHolder> binder;

    @Inject
    public EventsAdapter(EventBinderImpl binder) {
        super(binder);
        this.binder = binder;
        logMethod(TAG, this);
    }

    public void setupBinder(
          Context ctx, Runnable onAuthFail,
          OnEventClickListener listener) {
        binder.setup(ctx, onAuthFail, listener);
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
