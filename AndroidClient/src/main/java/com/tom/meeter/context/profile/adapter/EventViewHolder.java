package com.tom.meeter.context.profile.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.databinding.EventViewBinding;

public class EventViewHolder extends RecyclerView.ViewHolder {
    private final EventViewBinding binding;

    public EventViewHolder(EventViewBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(String name, String description) {
        binding.eventNameCardView.setText(name);
        binding.eventDescriptionCardView.setText(description);
    }
}
