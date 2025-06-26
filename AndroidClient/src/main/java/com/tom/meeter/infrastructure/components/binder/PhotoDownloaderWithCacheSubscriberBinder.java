package com.tom.meeter.infrastructure.components.binder;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.subscriber.Subscriber;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.components.adapter.OnSubscribeUnsubscribeClickListener;
import com.tom.meeter.infrastructure.components.adapter.OnUserClickListener;
import com.tom.meeter.infrastructure.components.viewholder.SubscriberViewHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PhotoDownloaderWithCacheSubscriberBinder
      implements SubscriberBinder<SubscriberViewHolder> {

    private static final String TAG = PhotoDownloaderWithCacheSubscriberBinder.class.getCanonicalName();

    private final Context ctx;
    private final ImageDownloader imageDownloader;
    private final Runnable onAuthFail;
    private final Map<String, Bitmap> imagesCache = new ConcurrentHashMap<>();

    private OnUserClickListener userClickListener;
    private OnSubscribeUnsubscribeClickListener subUnSubClickListener;

    public PhotoDownloaderWithCacheSubscriberBinder(
          Context ctx, ImageDownloader imgDownloader,
          Runnable onAuthFail) {
        logMethod(TAG, this);
        this.ctx = ctx;
        this.imageDownloader = imgDownloader;
        this.onAuthFail = onAuthFail;
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
        if (photoPath == null) {
            holder.bind(
                  target.isAmISubscribedTo(),
                  user.getName(), user.getSurname(), null,
                  (v) -> sendUserClickEvent(user),
                  (v) -> sendSubUnSubEvent(target, holder)
            );
            return;
        }
        Bitmap circledPhotoCache = imagesCache.get(photoPath);
        holder.bind(
              target.isAmISubscribedTo(),
              user.getName(), user.getSurname(), circledPhotoCache,
              (v) -> sendUserClickEvent(user),
              (v) -> sendSubUnSubEvent(target, holder)
        );
        if (circledPhotoCache != null) {
            return;
        }
        imageDownloader.downloadUserImage(
              photoPath, ctx, ImagesHelper::circleImage,
              photo -> {
                  holder.updatePhoto(photo);
                  imagesCache.put(photoPath, photo);
                  Log.d(TAG, "PhotoDownloaderWithCacheUserBinder: user " +
                        "image downloaded for [" + photoPath + "], cache updated.");
              },
              onAuthFail);
    }

    private void sendUserClickEvent(UserDTO user) {
        if (userClickListener != null) {
            userClickListener.onClick(user);
        }
    }

    private void sendSubUnSubEvent(
          Subscriber sub, SubscriberViewHolder holder) {
        if (subUnSubClickListener != null) {
            subUnSubClickListener.onSubUnSub(
                  sub, holder.getBindingAdapterPosition());
        }
    }
}
