package com.tom.meeter.infrastructure.components.binder;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.infrastructure.common.ImagesHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class PhotoWithCacheDownloader {

    private static final String TAG = PhotoWithCacheDownloader.class.getCanonicalName();

    protected final Context ctx;
    protected final ImageDownloader imgDownloader;
    protected final Runnable onAuthFail;
    protected final Map<String, Bitmap> cache = new ConcurrentHashMap<>();

    protected PhotoWithCacheDownloader(
          Context ctx, ImageDownloader imgDownloader, Runnable onAuthFail) {
        this.ctx = ctx;
        this.imgDownloader = imgDownloader;
        this.onAuthFail = onAuthFail;
    }

    protected void loadUserPhoto(
          String photoPath, Consumer<Bitmap> onImageReady) {
        Bitmap cached = cache.get(photoPath);
        if (cached != null) {
            onImageReady.accept(cached);
            return;
        }

        imgDownloader.downloadUserImage(
              photoPath, ctx, ImagesHelper::circleImage,
              photo -> {
                  cache.put(photoPath, photo);
                  onImageReady.accept(photo);
                  Log.d(TAG, "PhotoWithCacheDownloader: user image downloaded for ["
                        + photoPath + "], cache updated.");
              }, onAuthFail
        );
    }
}