package com.tom.meeter.infrastructure.components.binder;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.infrastructure.components.adapter.OnUserClickListener;

public interface UserBinder<T extends RecyclerView.ViewHolder>
      extends BaseUserBinder<T> {
    void setup(OnUserClickListener userClickListener);
}
