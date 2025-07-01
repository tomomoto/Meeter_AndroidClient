package com.tom.meeter.infrastructure.components.binder;

import android.content.Context;
import android.graphics.Bitmap;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.infrastructure.components.UserImageDownloader;
import com.tom.meeter.infrastructure.components.adapter.OnUserClickListener;
import com.tom.meeter.infrastructure.components.viewholder.UserViewHolder;

import javax.inject.Inject;

public class UserBinderImpl implements UserBinder<UserViewHolder> {

    private final UserImageDownloader userImageDownloader;

    private OnUserClickListener listener;

    @Inject
    public UserBinderImpl(
          UserImageDownloader userImageDownloader) {
        this.userImageDownloader = userImageDownloader;
    }

    @Override
    public void setup(
          Context ctx, Runnable onAuthFail,
          OnUserClickListener listener) {
        this.listener = listener;
        userImageDownloader.setup(ctx, onAuthFail);
    }

    @Override
    public void bind(UserViewHolder holder, UserDTO user) {
        String photoPath = user.getPhotoPath();

        Bitmap cached = photoPath != null
              ? userImageDownloader.getCachedPhoto(photoPath) : null;

        holder.bind(
              user.getName(), user.getSurname(), cached,
              v -> listener.onClick(user));

        if (photoPath != null && cached == null) {
            userImageDownloader.loadPhoto(photoPath, holder::updatePhoto);
        }
    }
}
