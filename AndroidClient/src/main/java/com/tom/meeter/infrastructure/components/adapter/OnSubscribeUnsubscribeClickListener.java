package com.tom.meeter.infrastructure.components.adapter;

import com.tom.meeter.context.profile.subscriber.Subscriber;

public interface OnSubscribeUnsubscribeClickListener {
    void onSubUnsub(Subscriber sub, int position);
}
