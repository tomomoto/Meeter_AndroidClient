package com.tom.meeter.infrastructure.components.adapter;

import com.tom.meeter.context.profile.subscriber.Subscriber;

public interface OnSubscribeUnsubscribeClickListener {
    void onSubUnSub(Subscriber sub, int position);
}
