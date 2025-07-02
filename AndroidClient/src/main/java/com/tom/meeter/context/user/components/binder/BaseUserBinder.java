package com.tom.meeter.context.user.components.binder;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.infrastructure.components.binder.BaseViewHolderBinder;

public interface BaseUserBinder<T extends RecyclerView.ViewHolder>
      extends BaseViewHolderBinder<T, UserDTO> {

    void bind(T holder, UserDTO target);
}
