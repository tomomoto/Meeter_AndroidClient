package com.tom.meeter.infrastructure.components.binder;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.components.adapter.OnEventClickListener;

public interface EventBinder<T extends RecyclerView.ViewHolder>
      extends BaseViewHolderBinder<T, EventDTO> {
    void bind(T holder, EventDTO event);
    void setup(Context ctx, Runnable onAuthFail, OnEventClickListener listener);
}
