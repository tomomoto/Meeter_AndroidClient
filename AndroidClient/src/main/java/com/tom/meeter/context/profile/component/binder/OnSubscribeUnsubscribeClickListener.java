package com.tom.meeter.context.profile.component.binder;

import com.tom.meeter.context.profile.domain.Subscriber;

public interface OnSubscribeUnsubscribeClickListener {
    void onSubUnSub(Subscriber sub, int position);
}
