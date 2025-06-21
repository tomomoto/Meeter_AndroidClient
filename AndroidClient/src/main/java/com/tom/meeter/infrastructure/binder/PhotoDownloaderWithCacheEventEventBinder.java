package com.tom.meeter.infrastructure.binder;

import static com.tom.meeter.infrastructure.Image.ImagesHelper.circleImage;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.adapter.OnEventClickListener;
import com.tom.meeter.infrastructure.viewholder.EventViewHolder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PhotoDownloaderWithCacheEventEventBinder
      implements ViewHolderEventBinder<EventViewHolder> {

    private static final String TAG = PhotoDownloaderWithCacheEventEventBinder.class.getCanonicalName();

    private final Context ctx;
    private final ImageDownloader imageDownloader;
    private final OnEventClickListener listener;
    private final Runnable onAuthFail;
    private final Map<String, Bitmap> imagesCache = new ConcurrentHashMap<>();

    public PhotoDownloaderWithCacheEventEventBinder(
          Context ctx, ImageDownloader imgDownloader,
          OnEventClickListener listener, Runnable onAuthFail) {
        logMethod(TAG, this);
        this.ctx = ctx;
        this.imageDownloader = imgDownloader;
        this.listener = listener;
        this.onAuthFail = onAuthFail;
    }

    @Override
    public void bind(EventViewHolder holder, EventDTO event) {
        String photoPath = event.getPhotoPath();
        if (photoPath == null) {
            holder.bind(
                  event.getName(), event.getDescription(), null,
                  (v) -> listener.onEventClick(event));
            return;
        }
        Bitmap circledPhotoCache = imagesCache.get(photoPath);
        holder.bind(
              event.getName(), event.getDescription(), circledPhotoCache,
              (v) -> listener.onEventClick(event));
        if (circledPhotoCache != null) {
            return;
        }
        imageDownloader.downloadEventImage(
              photoPath, ctx,
              photo -> {
                  Bitmap circled = circleImage(photo);
                  holder.updatePhoto(circled);
                  imagesCache.put(photoPath, circled);
                  Log.d(TAG, "PhotoDownloaderWithCacheEventEventBinder: event " +
                        "image downloaded for [" + photoPath + "], cache updated.");
              },
              onAuthFail);
    }
}
