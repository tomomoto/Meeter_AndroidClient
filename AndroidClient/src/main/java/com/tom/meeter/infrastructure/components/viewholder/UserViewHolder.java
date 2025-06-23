package com.tom.meeter.infrastructure.components.viewholder;

import android.graphics.Bitmap;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.databinding.ActivityUserSubscriberItemBinding;

public class UserViewHolder extends RecyclerView.ViewHolder {

    private final ActivityUserSubscriberItemBinding binding;

    public UserViewHolder(ActivityUserSubscriberItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(
          String name, String surname, Bitmap photo,
          View.OnClickListener cardClickListener) {
        binding.subscriberName.setText(name + " " + surname);
        if (photo != null) {
            binding.photo.setImageBitmap(photo);
        }
        binding.subCard.setOnClickListener(cardClickListener);
    }

    public void updatePhoto(Bitmap photo) {
        binding.photo.setImageBitmap(photo);
    }
}
