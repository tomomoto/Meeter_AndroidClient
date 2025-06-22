package com.tom.meeter.infrastructure.components.binder;

import static com.tom.meeter.infrastructure.common.ImagesHelper.circleImage;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.subscriber.Subscriber;
import com.tom.meeter.infrastructure.components.adapter.OnSubscribeUnsubscribeClickListener;
import com.tom.meeter.infrastructure.components.adapter.OnUserClickListener;
import com.tom.meeter.infrastructure.components.viewholder.SubscriberViewHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PhotoDownloaderWithCacheSubscriberBinder implements SubscriberBinder<SubscriberViewHolder> {

    private static final String TAG = PhotoDownloaderWithCacheSubscriberBinder.class.getCanonicalName();

    private final Context ctx;
    private final ImageDownloader imageDownloader;
    private final OnUserClickListener userClickListener;
    private final OnSubscribeUnsubscribeClickListener subUnsubClickListener;
    private final Runnable onAuthFail;
    private final Map<String, Bitmap> imagesCache = new ConcurrentHashMap<>();

    public PhotoDownloaderWithCacheSubscriberBinder(
          Context ctx, ImageDownloader imgDownloader,
          OnUserClickListener listener,
          OnSubscribeUnsubscribeClickListener subUnsubClickListener,
          Runnable onAuthFail) {
        logMethod(TAG, this);
        this.ctx = ctx;
        this.imageDownloader = imgDownloader;
        this.userClickListener = listener;
        this.subUnsubClickListener = subUnsubClickListener;
        this.onAuthFail = onAuthFail;
    }

    @Override
    public void bind(SubscriberViewHolder holder, Subscriber target) {
        UserDTO user = target.getUser();
        String photoPath = user.getPhotoPath();
        if (photoPath == null) {
            holder.bind(
                  target.isAmISubscribedTo(),
                  user.getName(), user.getSurname(), null,
                  (v) -> userClickListener.onClick(user),
                  (v) -> subUnsubClickListener.onSubUnsub(target, holder.getBindingAdapterPosition()));
            return;
        }
        Bitmap circledPhotoCache = imagesCache.get(photoPath);
        holder.bind(
              target.isAmISubscribedTo(),
              user.getName(), user.getSurname(), circledPhotoCache,
              (v) -> userClickListener.onClick(user),
              (v) -> subUnsubClickListener.onSubUnsub(target, holder.getBindingAdapterPosition()));
        if (circledPhotoCache != null) {
            return;
        }
        imageDownloader.downloadUserImage(
              photoPath, ctx,
              photo -> {
                  Bitmap circled = circleImage(photo);
                  holder.updatePhoto(circled);
                  imagesCache.put(photoPath, circled);
                  Log.d(TAG, "PhotoDownloaderWithCacheUserBinder: user " +
                        "image downloaded for [" + photoPath + "], cache updated.");
              },
              onAuthFail);
    }
}
