package com.tom.meeter.infrastructure.components;

import android.content.Context;
import android.graphics.Bitmap;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.infrastructure.common.ImagesHelper;

import java.util.function.Consumer;

public class EventImageDownloader extends PhotoWithCacheDownloader {

    public EventImageDownloader(
          Context ctx, ImageDownloader imgDownloader, Runnable onAuthFail) {
        super(ctx, imgDownloader, onAuthFail);
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
