package com.tom.meeter.infrastructure.components.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.profile.subscriber.Subscriber;
import com.tom.meeter.infrastructure.components.binder.SubscriberBinder;

public abstract class BaseSubscriberAdapter<T extends RecyclerView.ViewHolder>
      extends BaseAdapter<T, Subscriber> {

    public BaseSubscriberAdapter(SubscriberBinder<T> binder) {
        super(binder);
    }
}
