package com.tom.meeter.context.profile.component.binder;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.infrastructure.components.SetContext;
import com.tom.meeter.infrastructure.components.SetOnAuthFailAction;
import com.tom.meeter.infrastructure.components.adapter.OnUserClickListener;

public interface SubscriberBinder<T extends RecyclerView.ViewHolder>
      extends SetContext, SetOnAuthFailAction,
      BaseSubscriberBinder<T> {

    void setOnSubscribeUnsubscribeClickListener(
          OnSubscribeUnsubscribeClickListener subUnSubClickListener);

    void setOnUserClickListener(OnUserClickListener userClickListener);
}
