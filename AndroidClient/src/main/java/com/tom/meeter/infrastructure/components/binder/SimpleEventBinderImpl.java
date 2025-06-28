package com.tom.meeter.infrastructure.components.binder;

import android.content.Context;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.components.adapter.OnEventClickListener;
import com.tom.meeter.infrastructure.components.viewholder.CardItemHolder;

public class SimpleEventBinderImpl implements EventBinder<CardItemHolder> {

    private static final String TAG = SimpleEventBinderImpl.class.getCanonicalName();

    private final Context ctx;
    private final ImageDownloader imageDownloader;
    private final OnEventClickListener listener;
    private final Runnable onAuthFail;

    public SimpleEventBinderImpl(
          Context ctx, ImageDownloader imageDownloader,
          OnEventClickListener listener, Runnable onAuthFail) {
        this.ctx = ctx;
        this.imageDownloader = imageDownloader;
        this.listener = listener;
        this.onAuthFail = onAuthFail;
    }

    @Override
    public void bind(CardItemHolder holder, EventDTO event) {
        holder.bind(event.getName(), null, (view) -> listener.onClick(event));
        String photoPath = event.getPhotoPath();
        if (photoPath == null) {
            return;
        }
        imageDownloader.downloadEventImage(
              photoPath, ctx, ImagesHelper::circleImage,
              holder::updatePhoto, onAuthFail);
    }
}
