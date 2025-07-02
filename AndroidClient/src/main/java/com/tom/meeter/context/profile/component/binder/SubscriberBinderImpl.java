package com.tom.meeter.context.profile.component.binder;

import android.content.Context;
import android.graphics.Bitmap;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.component.viewholder.SubscriberViewHolder;
import com.tom.meeter.context.profile.domain.Subscriber;
import com.tom.meeter.infrastructure.components.adapter.OnUserClickListener;
import com.tom.meeter.infrastructure.components.downloader.UserImageDownloader;

import javax.inject.Inject;

public class SubscriberBinderImpl
      implements SubscriberBinder<SubscriberViewHolder> {

    private final UserImageDownloader userImageDownloader;

    private OnSubscribeUnsubscribeClickListener subUnSubClickListener;
    private OnUserClickListener userClickListener;

    @Inject
    public SubscriberBinderImpl(
          UserImageDownloader userImageDownloader) {
        this.userImageDownloader = userImageDownloader;
    }

    @Override
    public void setOnSubscribeUnsubscribeClickListener(
          OnSubscribeUnsubscribeClickListener subUnSubClickListener) {
        this.subUnSubClickListener = subUnSubClickListener;
    }

    @Override
    public void setOnUserClickListener(
          OnUserClickListener userClickListener) {
        this.userClickListener = userClickListener;
    }

    @Override
    public void setContext(Context ctx) {
        userImageDownloader.setContext(ctx);
    }

    @Override
    public void setOnAuthFailAction(Runnable onAuthFail) {
        userImageDownloader.setOnAuthFailAction(onAuthFail);
    }

    @Override
    public void bind(SubscriberViewHolder holder, Subscriber target) {
        UserDTO user = target.getUser();
        String photoPath = user.getPhotoPath();

        Bitmap cached = photoPath != null
              ? userImageDownloader.getCachedPhoto(photoPath) : null;

        holder.bind(
              target.isAmISubscribedTo(),
              user.getName(), user.getSurname(), cached,
              v -> sendUserClickEvent(user),
              v -> sendSubUnSubEvent(target, holder)
        );

        if (photoPath != null && cached == null) {
            userImageDownloader.loadPhoto(photoPath, holder::updatePhoto);
        }
    }

    private void sendUserClickEvent(UserDTO user) {
        if (userClickListener != null) {
            userClickListener.onClick(user);
        }
    }

    private void sendSubUnSubEvent(Subscriber sub, SubscriberViewHolder holder) {
        if (subUnSubClickListener != null) {
            subUnSubClickListener.onSubUnSub(sub, holder.getBindingAdapterPosition());
        }
    }
}
