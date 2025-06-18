package com.tom.meeter.context.profile.adapter;

import android.graphics.Bitmap;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.databinding.EventViewBinding;

public class EventViewHolder extends RecyclerView.ViewHolder {

    private final EventViewBinding binding;

    public EventViewHolder(EventViewBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void init(
          String name, String description, Bitmap photo,
          View.OnClickListener clickListener) {
        binding.eventCardView.setOnClickListener(clickListener);
        binding.eventNameCardView.setText(name);
        binding.eventDescriptionCardView.setText(description);
        binding.eventPhotoCardView.setImageBitmap(photo);
    }

    public void updatePhoto(Bitmap photo) {
        binding.eventPhotoCardView.setImageBitmap(photo);
    }
}
