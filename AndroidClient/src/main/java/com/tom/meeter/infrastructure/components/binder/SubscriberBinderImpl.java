package com.tom.meeter.infrastructure.components.binder;

import android.content.Context;
import android.graphics.Bitmap;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.subscriber.Subscriber;
import com.tom.meeter.infrastructure.components.UserImageDownloader;
import com.tom.meeter.infrastructure.components.adapter.OnSubscribeUnsubscribeClickListener;
import com.tom.meeter.infrastructure.components.adapter.OnUserClickListener;
import com.tom.meeter.infrastructure.components.viewholder.SubscriberViewHolder;

public class SubscriberBinderImpl extends UserImageDownloader
      implements SubscriberBinder<SubscriberViewHolder> {

    private OnUserClickListener userClickListener;
    private OnSubscribeUnsubscribeClickListener subUnSubClickListener;

    public SubscriberBinderImpl(
          Context ctx, ImageDownloader imgDownloader, Runnable onAuthFail) {
        super(ctx, imgDownloader, onAuthFail);
    }

    @Override
    public void setup(
          OnSubscribeUnsubscribeClickListener subUnSubClickListener,
          OnUserClickListener userClickListener) {
        this.subUnSubClickListener = subUnSubClickListener;
        this.userClickListener = userClickListener;
    }

    @Override
    public void bind(SubscriberViewHolder holder, Subscriber target) {
        UserDTO user = target.getUser();
        String photoPath = user.getPhotoPath();

        Bitmap cached = photoPath != null ? cache.get(photoPath) : null;

        holder.bind(
              target.isAmISubscribedTo(),
              user.getName(), user.getSurname(), cached,
              v -> sendUserClickEvent(user),
              v -> sendSubUnSubEvent(target, holder)
        );

        if (photoPath != null && cached == null) {
            loadPhoto(photoPath, holder::updatePhoto);
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
