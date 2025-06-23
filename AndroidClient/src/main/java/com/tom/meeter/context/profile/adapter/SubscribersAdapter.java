package com.tom.meeter.context.profile.adapter;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.databinding.ActivityProfileSubscriberItemBinding;
import com.tom.meeter.infrastructure.components.adapter.BaseSubscriberAdapter;
import com.tom.meeter.infrastructure.components.binder.SubscriberBinder;
import com.tom.meeter.infrastructure.components.viewholder.SubscriberViewHolder;

public class SubscribersAdapter extends BaseSubscriberAdapter<SubscriberViewHolder> {

    private static final String TAG = SubscribersAdapter.class.getCanonicalName();

    public SubscribersAdapter(SubscriberBinder<SubscriberViewHolder> binder) {
        super(binder);
        logMethod(TAG, this);
    }

    @Override
    public SubscriberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        logMethod(TAG, this);
        return new SubscriberViewHolder(
              ActivityProfileSubscriberItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        logMethod(TAG, this);
    }
}
