package com.tom.meeter.infrastructure.components.binder;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.profile.subscriber.Subscriber;

public interface SubscriberBinder<T extends RecyclerView.ViewHolder>
      extends BaseViewHolderBinder<T, Subscriber> {

    void bind(T holder, Subscriber target);
}
