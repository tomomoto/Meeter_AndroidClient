package com.tom.meeter.infrastructure.viewholder;

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

    public void bind(
          String name, String description, Bitmap photo,
          View.OnClickListener clickListener) {
        binding.eventNameCardView.setText(name);
        binding.eventDescriptionCardView.setText(description);
        binding.eventPhotoCardView.setImageBitmap(photo);
        binding.eventCardView.setOnClickListener(clickListener);
    }

    public void updatePhoto(Bitmap photo) {
        binding.eventPhotoCardView.setImageBitmap(photo);
    }
}
