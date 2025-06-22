package com.tom.meeter.infrastructure.components.binder;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.UserDTO;

public interface UserBinder<T extends RecyclerView.ViewHolder>
      extends BaseViewHolderBinder<T, UserDTO> {

    void bind(T holder, UserDTO target);
}
