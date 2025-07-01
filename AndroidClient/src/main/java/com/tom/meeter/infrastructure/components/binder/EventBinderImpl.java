package com.tom.meeter.infrastructure.components.binder;

import static com.tom.meeter.infrastructure.common.CommonHelper.handleEventStatus;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.infrastructure.components.EventImageDownloader;
import com.tom.meeter.infrastructure.components.UserWithCacheDownloader;
import com.tom.meeter.infrastructure.components.adapter.OnEventClickListener;
import com.tom.meeter.infrastructure.components.viewholder.EventViewHolder;

public class EventBinderImpl implements EventBinder<EventViewHolder> {

    private static final String TAG = EventBinderImpl.class.getCanonicalName();

    private final Context ctx;
    private final String auth;
    private final EventImageDownloader eventImageDownloader;
    private final UserWithCacheDownloader userDownloader;
    private final OnEventClickListener listener;

    public EventBinderImpl(
          Context ctx, String auth, ImageDownloader imgDownloader,
          UserService service, OnEventClickListener listener,
          Runnable onAuthFail) {
        this.ctx = ctx;
        this.auth = auth;
        this.listener = listener;
        this.eventImageDownloader = new EventImageDownloader(
              ctx, imgDownloader, onAuthFail);
        this.userDownloader = new UserWithCacheDownloader(
              ctx, service, onAuthFail);
    }

    @Override
    public void bind(EventViewHolder holder, EventDTO event) {
        Log.d(TAG, "Current thread: " + Thread.currentThread().getName());
        String photoPath = event.getPhotoPath();

        Bitmap cachedPhoto = photoPath != null
              ? eventImageDownloader.getCachedPhoto(event.getPhotoPath()) : null;

        UserDTO userCache = userDownloader.getUserCache(event.getCreatorId());
        Log.d(TAG, "User by id " + event.getCreatorId() + " is " + userCache);

        holder.bind(
              event.getName(), event.getDescription(),
              cachedPhoto, getCreator(userCache),
              v -> listener.onClick(event),
              v -> handleEventStatus(ctx, v, event.getStatus()));

        if (userCache == null) {
            userDownloader.loadUser(
                  auth, event.getCreatorId(),
                  u -> {
                      Log.d(TAG, "Downloaded user by id " + event.getCreatorId() + " is " + u);
                      holder.updateCreator(getCreator(u));
                  });
        }
        if (photoPath != null && cachedPhoto == null) {
            eventImageDownloader.loadPhoto(photoPath, holder::updatePhoto);
        }
    }

    private static String getCreator(UserDTO user) {
        return user == null ? null : user.getName() + " " + user.getSurname();
    }
}
