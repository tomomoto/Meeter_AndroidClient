package com.tom.meeter.infrastructure.components.viewholder;

import android.graphics.Bitmap;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.databinding.ActivityProfileSubscriberItemBinding;

public class UserViewHolder extends RecyclerView.ViewHolder {

    private final ActivityProfileSubscriberItemBinding binding;

    public UserViewHolder(ActivityProfileSubscriberItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(
          String name, String surname, Bitmap photo,
          View.OnClickListener cardClickListener,
          View.OnClickListener buttonClickListener) {
        binding.subscriberName.setText(name);
        binding.subscriberSurname.setText(surname);
        binding.photo.setImageBitmap(photo);

        binding.subCard.setOnClickListener(cardClickListener);
        binding.buttonSubscribe.setOnClickListener(buttonClickListener);
    }

    public void updatePhoto(Bitmap photo) {
        binding.photo.setImageBitmap(photo);
    }
}
