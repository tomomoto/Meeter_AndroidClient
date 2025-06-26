package com.tom.meeter.infrastructure.components.binder;

import android.content.Context;
import android.graphics.Bitmap;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.infrastructure.components.adapter.OnUserClickListener;
import com.tom.meeter.infrastructure.components.viewholder.UserViewHolder;

public class UserBinderImpl extends PhotoWithCacheDownloader
      implements UserBinder<UserViewHolder> {

    private OnUserClickListener userClickListener;

    public UserBinderImpl(
          Context ctx, ImageDownloader imgDownloader,
          Runnable onAuthFail) {
        super(ctx, imgDownloader, onAuthFail);
    }

    @Override
    public void bind(UserViewHolder holder, UserDTO user) {
        String photoPath = user.getPhotoPath();
        if (photoPath == null) {
            holder.bind(
                  user.getName(),
                  user.getSurname(),
                  null,
                  v -> userClickListener.onClick(user));
            return;
        }

        Bitmap cached = cache.get(photoPath);
        holder.bind(
              user.getName(),
              user.getSurname(),
              cached, v -> userClickListener.onClick(user));

        if (cached == null) {
            loadUserPhoto(photoPath, holder::updatePhoto);
        }
    }

    @Override
    public void setup(OnUserClickListener userClickListener) {
        this.userClickListener = userClickListener;
    }
}
