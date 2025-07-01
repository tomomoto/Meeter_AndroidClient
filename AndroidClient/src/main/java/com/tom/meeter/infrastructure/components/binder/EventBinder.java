package com.tom.meeter.infrastructure.components.binder;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.components.SetContext;
import com.tom.meeter.infrastructure.components.SetOnAuthFailAction;
import com.tom.meeter.infrastructure.components.adapter.OnEventClickListener;

public interface EventBinder<T extends RecyclerView.ViewHolder>
      extends SetContext, SetOnAuthFailAction,
      BaseViewHolderBinder<T, EventDTO> {
    void bind(T holder, EventDTO event);

    void setupOnEventClickListener(OnEventClickListener listener);
}
