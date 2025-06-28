package com.tom.meeter.infrastructure.components;

import android.content.Context;
import android.graphics.Bitmap;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.infrastructure.common.ImagesHelper;

import java.util.function.Consumer;

public class UserImageDownloader extends PhotoWithCacheDownloader {

    protected UserImageDownloader(
          Context ctx, ImageDownloader imgDownloader, Runnable onAuthFail) {
        super(ctx, imgDownloader, onAuthFail);
    }

    @Override
    protected void downloadImage(
          String photoPath, Consumer<Bitmap> onDownloaded) {
        imgDownloader.downloadUserImage(
              photoPath, ctx,
              ImagesHelper::circleImage,
              onDownloaded, onAuthFail);
    }
}
