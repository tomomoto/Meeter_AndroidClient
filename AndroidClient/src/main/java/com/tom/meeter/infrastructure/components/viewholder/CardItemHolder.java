package com.tom.meeter.infrastructure.components.viewholder;

import android.graphics.Bitmap;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.tom.meeter.databinding.CardItemBinding;

public class CardItemHolder extends RecyclerView.ViewHolder {

    private final CardItemBinding binding;

    public CardItemHolder(CardItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(
          String name, Bitmap photo, View.OnClickListener clickListener) {
        binding.textView.setText(name);
        binding.imageView.setImageBitmap(photo);
        binding.card.setOnClickListener(clickListener);
    }

    public void updatePhoto(Bitmap photo) {
        binding.imageView.setImageBitmap(photo);
    }
}
