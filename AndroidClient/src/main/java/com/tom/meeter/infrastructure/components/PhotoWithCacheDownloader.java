package com.tom.meeter.infrastructure.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tom.meeter.context.image.ImageDownloader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class PhotoWithCacheDownloader {

    private static final String TAG = PhotoWithCacheDownloader.class.getCanonicalName();

    protected ImageDownloader imgDownloader;

    protected Context ctx;
    protected Runnable onAuthFail;

    protected final Map<String, Bitmap> cache = new ConcurrentHashMap<>();

    protected PhotoWithCacheDownloader(ImageDownloader imgDownloader) {
        this.imgDownloader = imgDownloader;
    }

    public void setup(Context ctx, Runnable onAuthFail) {
        this.ctx = ctx;
        this.onAuthFail = onAuthFail;
    }

    public Bitmap getCachedPhoto(String photoPath) {
        return cache.get(photoPath);
    }

    public void loadPhoto(String photoPath, Consumer<Bitmap> onImageReady) {
        Bitmap cached = cache.get(photoPath);
        if (cached != null) {
            onImageReady.accept(cached);
            return;
        }

        downloadImage(
              photoPath, photo -> {
                  cache.put(photoPath, photo);
                  onImageReady.accept(photo);
                  Log.d(TAG, getClass().getSimpleName() +
                        ": image downloaded for [" + photoPath + "], cache updated.");
              });
    }

    protected abstract void downloadImage(String photoPath, Consumer<Bitmap> onDownloaded);
}
