package com.tom.meeter.infrastructure.components.binder;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.infrastructure.components.adapter.OnSubscribeUnsubscribeClickListener;
import com.tom.meeter.infrastructure.components.adapter.OnUserClickListener;

public interface SubscriberBinder<T extends RecyclerView.ViewHolder>
      extends BaseSubscriberBinder<T> {
    void setup(
          Context ctx, Runnable onAuthFail,
          OnSubscribeUnsubscribeClickListener subUnSubClickListener,
          OnUserClickListener userClickListener);
}
