package com.tom.meeter.infrastructure.binder;

import static com.tom.meeter.infrastructure.Image.ImagesHelper.circleImage;

import android.content.Context;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.adapter.OnEventClickListener;
import com.tom.meeter.infrastructure.viewholder.CardItemHolder;

public class PhotoDownloaderEventBinder
      implements ViewHolderEventBinder<CardItemHolder> {

    private static final String TAG = PhotoDownloaderEventBinder.class.getCanonicalName();

    private final Context ctx;
    private final ImageDownloader imageDownloader;
    private final OnEventClickListener listener;
    private final Runnable onAuthFail;

    public PhotoDownloaderEventBinder(
          Context ctx, ImageDownloader imageDownloader,
          OnEventClickListener listener, Runnable onAuthFail) {
        this.ctx = ctx;
        this.imageDownloader = imageDownloader;
        this.listener = listener;
        this.onAuthFail = onAuthFail;
    }

    @Override
    public void bind(CardItemHolder holder, EventDTO event) {
        holder.bind(event.getName(), null, (view) -> listener.onEventClick(event));
        String photoPath = event.getPhotoPath();
        if (photoPath == null) {
            return;
        }
        imageDownloader.downloadEventImage(
              photoPath, ctx, (photo) -> holder.updatePhoto(circleImage(photo)), onAuthFail);
    }
}
