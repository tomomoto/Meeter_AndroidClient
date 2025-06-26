package com.tom.meeter.infrastructure.components.binder;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.components.adapter.OnUserClickListener;
import com.tom.meeter.infrastructure.components.viewholder.UserViewHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PhotoDownloaderWithCacheUserBinder implements UserBinder<UserViewHolder> {

    private static final String TAG = PhotoDownloaderWithCacheEventBinder.class.getCanonicalName();

    private final Context ctx;
    private final ImageDownloader imageDownloader;
    private final OnUserClickListener userClickListener;
    private final Runnable onAuthFail;
    private final Map<String, Bitmap> imagesCache = new ConcurrentHashMap<>();

    public PhotoDownloaderWithCacheUserBinder(
          Context ctx, ImageDownloader imgDownloader,
          OnUserClickListener listener,
          Runnable onAuthFail) {
        logMethod(TAG, this);
        this.ctx = ctx;
        this.imageDownloader = imgDownloader;
        this.userClickListener = listener;
        this.onAuthFail = onAuthFail;
    }

    @Override
    public void bind(UserViewHolder holder, UserDTO user) {
        String photoPath = user.getPhotoPath();
        if (photoPath == null) {
            holder.bind(
                  user.getName(), user.getSurname(), null,
                  (v) -> userClickListener.onClick(user));
            return;
        }
        Bitmap circledPhotoCache = imagesCache.get(photoPath);
        holder.bind(
              user.getName(), user.getSurname(), circledPhotoCache,
              (v) -> userClickListener.onClick(user));
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
}
