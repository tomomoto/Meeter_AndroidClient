package com.tom.meeter.infrastructure.components.viewholder;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.databinding.EventViewBinding;

import java.util.function.Consumer;

public class EventViewHolder extends RecyclerView.ViewHolder {

    private final EventViewBinding binding;

    public EventViewHolder(EventViewBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(
          String name, String description, Bitmap photo, String creator,
          View.OnClickListener clickListener, Consumer<TextView> statusC) {
        binding.eventNameCardView.setText(name);
        binding.eventDescriptionCardView.setText(description);
        binding.eventCreatorCardView.setText(creator);
        binding.eventPhotoCardView.setImageBitmap(photo);
        binding.eventCardView.setOnClickListener(clickListener);
        statusC.accept(binding.eventStatusCardView);
    }

    public void updatePhoto(Bitmap photo) {
        binding.eventPhotoCardView.setImageBitmap(photo);
    }

    public void updateCreator(String creator) {
        binding.eventCreatorCardView.setText(creator);
    }
}
