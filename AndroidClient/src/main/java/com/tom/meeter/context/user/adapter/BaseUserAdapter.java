package com.tom.meeter.context.user.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.infrastructure.components.adapter.BaseAdapter;
import com.tom.meeter.infrastructure.components.binder.UserBinder;

public abstract class BaseUserAdapter<T extends RecyclerView.ViewHolder>
      extends BaseAdapter<T, UserDTO> {

    public BaseUserAdapter(UserBinder<T> binder) {
        super(binder);
    }
}
