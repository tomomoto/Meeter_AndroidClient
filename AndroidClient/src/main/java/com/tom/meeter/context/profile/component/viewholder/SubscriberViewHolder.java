package com.tom.meeter.context.profile.component.viewholder;

import android.graphics.Bitmap;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.R;
import com.tom.meeter.databinding.ActivityProfileSubscriberItemBinding;

public class SubscriberViewHolder extends RecyclerView.ViewHolder {

    private final ActivityProfileSubscriberItemBinding binding;

    public SubscriberViewHolder(ActivityProfileSubscriberItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(
          boolean isAmSubscribedTo, String name, String surname, Bitmap photo,
          View.OnClickListener cardClickListener,
          View.OnClickListener subUnSubClickListener) {
        binding.subscriberName.setText(name + " " + surname);
        if (photo != null) {
            binding.photo.setImageBitmap(photo);
        }

        binding.subUnsubBtn.setOnClickListener(subUnSubClickListener);
        binding.subCard.setOnClickListener(cardClickListener);
        binding.subUnsubBtn.setText(isAmSubscribedTo ? R.string.unsubscribe : R.string.subscribe);
    }

    public void updatePhoto(Bitmap photo) {
        binding.photo.setImageBitmap(photo);
    }
}
