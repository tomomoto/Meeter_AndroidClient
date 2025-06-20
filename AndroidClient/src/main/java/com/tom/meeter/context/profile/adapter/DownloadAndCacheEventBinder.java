package com.tom.meeter.context.profile.adapter;

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

public class DownloadAndCacheEventBinder {

    private static final String TAG = DownloadAndCacheEventBinder.class.getCanonicalName();

    private final Fragment fragment;
    private final ImageDownloader imageDownloader;
    private final OnEventClickListener onEventClick;
    private final Map<String, Bitmap> imagesCache = new ConcurrentHashMap<>();

    public DownloadAndCacheEventBinder(
          Fragment fragment, ImageDownloader imgDownloader,
          OnEventClickListener onEventClick) {
        logMethod(TAG, this);
        this.fragment = fragment;
        this.imageDownloader = imgDownloader;
        this.onEventClick = onEventClick;
    }

    public void bind(EventViewHolder holder, EventDTO event) {
        String photoPath = event.getPhotoPath();
        if (photoPath == null) {
            holder.init(event.getName(), event.getDescription(), null, (view) -> onEventClick.onEventClick(event));
            return;
        }
        Bitmap circledPhotoCache = imagesCache.get(photoPath);
        holder.init(event.getName(), event.getDescription(), null, (view) -> onEventClick.onEventClick(event));
        if (circledPhotoCache != null) {
            holder.updatePhoto(circledPhotoCache);
            return;
        }
        Log.d(TAG, "DownloadAndCacheEventBinder: downloading " +
              "event image for " + photoPath + " ...");
        imageDownloader.downloadEventImage(
              photoPath, fragment.getContext(),
              photo -> {
                  Bitmap circled = circleImage(photo);
                  holder.updatePhoto(circled);

                  imagesCache.put(photoPath, circled);
                  Log.d(TAG, "DownloadAndCacheEventBinder: event image downloaded for "
                        + photoPath + ", cache updated.");
              },
              () -> InfrastructureHelper.restartActivityFromFragment(fragment));
    }
}
