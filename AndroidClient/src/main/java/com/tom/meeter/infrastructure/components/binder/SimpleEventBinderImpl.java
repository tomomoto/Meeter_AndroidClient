package com.tom.meeter.infrastructure.components.binder;

import android.content.Context;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.infrastructure.common.ImagesHelper;
import com.tom.meeter.infrastructure.components.adapter.OnEventClickListener;
import com.tom.meeter.infrastructure.components.viewholder.CardItemHolder;

import javax.inject.Inject;

public class SimpleEventBinderImpl implements EventBinder<CardItemHolder> {

    private static final String TAG = SimpleEventBinderImpl.class.getCanonicalName();

    private final ImageDownloader imageDownloader;

    private Context ctx;
    private OnEventClickListener listener;
    private Runnable onAuthFail;

    @Inject
    public SimpleEventBinderImpl(ImageDownloader imageDownloader) {
        this.imageDownloader = imageDownloader;
    }

    @Override
    public void setup(
          Context ctx, Runnable onAuthFail,
          OnEventClickListener listener) {
        this.ctx = ctx;
        this.onAuthFail = onAuthFail;
        this.listener = listener;
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
