package com.tom.meeter.context.profile.adapter;

import static com.tom.meeter.context.event.activity.EventActivity.dispatchToEventActivity;
import static com.tom.meeter.infrastructure.Image.ImagesHelper.circleImage;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.common.InfrastructureHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadAndCacheEventBinder {

    private static final String TAG = DownloadAndCacheEventBinder.class.getCanonicalName();

    private final ImageDownloader imageDownloader;
    private final Fragment fragment;
    private final Map<String, Bitmap> imagesCache = new ConcurrentHashMap<>();

    public DownloadAndCacheEventBinder(Fragment fragment, ImageDownloader imgDownloader) {
        logMethod(TAG, this);
        this.fragment = fragment;
        this.imageDownloader = imgDownloader;
    }

    public void bind(EventViewHolder holder, EventDTO event) {
        String photoPath = event.getPhotoPath();
        if (photoPath == null) {
            holder.init(event.getName(), event.getDescription(), null, onEventClick(event));
            return;
        }
        Bitmap circledPhotoCache = imagesCache.get(photoPath);
        holder.init(event.getName(), event.getDescription(), null, onEventClick(event));
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

                  Log.d(TAG, "DownloadAndCacheEventBinder: event image downloaded for "
                        + photoPath + ", cache updated.");
                  imagesCache.put(photoPath, circled);
              },
              () -> InfrastructureHelper.restartActivityFromFragment(fragment));
    }

    private View.OnClickListener onEventClick(EventDTO event) {
        return v -> dispatchToEventActivity(fragment.getContext(), event.getId());
    }
}
