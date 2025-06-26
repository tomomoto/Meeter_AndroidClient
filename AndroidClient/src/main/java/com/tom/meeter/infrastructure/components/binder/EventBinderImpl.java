package com.tom.meeter.infrastructure.components.binder;

import android.content.Context;
import android.graphics.Bitmap;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.components.PhotoWithCacheDownloader;
import com.tom.meeter.infrastructure.components.adapter.OnEventClickListener;
import com.tom.meeter.infrastructure.components.viewholder.EventViewHolder;

import java.util.function.Consumer;

public class EventBinderImpl
      extends PhotoWithCacheDownloader
      implements EventBinder<EventViewHolder> {

    private final OnEventClickListener listener;

    public EventBinderImpl(
          Context ctx, ImageDownloader imgDownloader,
          OnEventClickListener listener, Runnable onAuthFail) {
        super(ctx, imgDownloader, onAuthFail);
        this.listener = listener;
    }

    @Override
    public void bind(EventViewHolder holder, EventDTO event) {
        String photoPath = event.getPhotoPath();

        Bitmap cached = photoPath != null ? cache.get(photoPath) : null;

        holder.bind(
              event.getName(), event.getDescription(), cached,
              v -> listener.onClick(event));

        if (photoPath != null && cached == null) {
            loadPhoto(photoPath, holder::updatePhoto);
        }
    }

    @Override
    protected void downloadImage(String photoPath, Consumer<Bitmap> onDownloaded) {
        imgDownloader.downloadEventImage(
              photoPath, ctx,
              ImagesHelper::circleImage,
              onDownloaded,
              onAuthFail);
    }
}
