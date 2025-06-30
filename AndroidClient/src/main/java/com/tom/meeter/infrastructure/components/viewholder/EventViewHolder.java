package com.tom.meeter.infrastructure.components.viewholder;

import static com.tom.meeter.infrastructure.common.CommonHelper.setRoundedBackground;

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
          String name, String description, String status, int colorRes,
          Bitmap photo, View.OnClickListener clickListener) {
        binding.eventNameCardView.setText(name);
        binding.eventDescriptionCardView.setText(description);
        binding.eventPhotoCardView.setImageBitmap(photo);
        binding.eventStatusCardView.setText(status);
        setRoundedBackground(binding.eventStatusCardView, colorRes, 6f);
        //binding.eventStatusCardView.setBackgroundColor(colorRes);
        //binding.eventStatusCardView.setBackgroundResource(colorRes);
        binding.eventCardView.setOnClickListener(clickListener);
    }

    public void updatePhoto(Bitmap photo) {
        binding.eventPhotoCardView.setImageBitmap(photo);
    }
}
