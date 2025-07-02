package com.tom.meeter.context.profile.component.binder;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.profile.domain.Subscriber;
import com.tom.meeter.infrastructure.components.binder.BaseViewHolderBinder;

public interface BaseSubscriberBinder<T extends RecyclerView.ViewHolder>
      extends BaseViewHolderBinder<T, Subscriber> {

    void bind(T holder, Subscriber target);
}
