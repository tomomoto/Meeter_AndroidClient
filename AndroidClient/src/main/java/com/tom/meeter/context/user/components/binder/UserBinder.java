package com.tom.meeter.context.user.components.binder;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.infrastructure.components.SetContext;
import com.tom.meeter.infrastructure.components.SetOnAuthFailAction;
import com.tom.meeter.infrastructure.components.adapter.OnUserClickListener;

public interface UserBinder<T extends RecyclerView.ViewHolder>
      extends SetContext, SetOnAuthFailAction,
      BaseUserBinder<T> {
    void setOnUserClickListener(OnUserClickListener listener);
}
