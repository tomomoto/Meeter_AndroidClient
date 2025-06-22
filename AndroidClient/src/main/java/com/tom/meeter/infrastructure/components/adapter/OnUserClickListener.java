package com.tom.meeter.infrastructure.components.adapter;

import com.tom.meeter.context.network.dto.UserDTO;

public interface OnUserClickListener extends BaseOnClickListener<UserDTO> {
    void onClick(UserDTO event);
}
