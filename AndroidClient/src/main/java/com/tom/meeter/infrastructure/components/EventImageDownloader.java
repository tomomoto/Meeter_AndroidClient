package com.tom.meeter.infrastructure.components;

import android.graphics.Bitmap;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.infrastructure.common.ImagesHelper;

import java.util.function.Consumer;

import javax.inject.Inject;

public class EventImageDownloader extends PhotoWithCacheDownloader {

    @Inject
    public EventImageDownloader(ImageDownloader imgDownloader) {
        super(imgDownloader);
    }

    @Override
    protected void downloadImage(
          String photoPath, Consumer<Bitmap> onDownloaded) {
        imgDownloader.downloadEventImage(
              photoPath, ctx,
              ImagesHelper::circleImage,
              onDownloaded, onAuthFail);
    }
}
