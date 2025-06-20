package com.tom.meeter.infrastructure.binder;

import static com.tom.meeter.infrastructure.Image.ImagesHelper.circleImage;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.adapter.OnEventClickListener;
import com.tom.meeter.infrastructure.common.InfrastructureHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PhotoDownloaderWithCacheEventBinder implements EventBinder {

    private static final String TAG = PhotoDownloaderWithCacheEventBinder.class.getCanonicalName();

    private final Fragment fragment;
    private final ImageDownloader imageDownloader;
    private final OnEventClickListener onEventClick;
    private final Map<String, Bitmap> imagesCache = new ConcurrentHashMap<>();

    public PhotoDownloaderWithCacheEventBinder(
          Fragment fragment, ImageDownloader imgDownloader,
          OnEventClickListener onEventClick) {
        logMethod(TAG, this);
        this.fragment = fragment;
        this.imageDownloader = imgDownloader;
        this.onEventClick = onEventClick;
    }

    @Override
    public void bind(EventViewHolder holder, EventDTO event) {
        String photoPath = event.getPhotoPath();
        if (photoPath == null) {
            holder.bind(
                  event.getName(), event.getDescription(), null,
                  (v) -> onEventClick.onEventClick(event));
            return;
        }
        Bitmap circledPhotoCache = imagesCache.get(photoPath);
        holder.bind(
              event.getName(), event.getDescription(), circledPhotoCache,
              (v) -> onEventClick.onEventClick(event));
        if (circledPhotoCache != null) {
            return;
        }
        imageDownloader.downloadEventImage(
              photoPath, fragment.getContext(),
              photo -> {
                  Bitmap circled = circleImage(photo);
                  holder.updatePhoto(circled);
                  imagesCache.put(photoPath, circled);
                  Log.d(TAG, "PhotoDownloaderWithCacheEventBinder: event image " +
                        "downloaded for " + photoPath + ", cache updated.");
              },
              () -> InfrastructureHelper.restartActivityFromFragment(fragment));
    }
}
