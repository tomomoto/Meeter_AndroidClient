package com.tom.meeter.context.profile.component.binder;

import static com.tom.meeter.infrastructure.common.CommonHelper.handleEventStatus;

import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.network.dto.EventDTO;
import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.component.viewholder.EventViewHolder;
import com.tom.meeter.infrastructure.components.adapter.OnEventClickListener;
import com.tom.meeter.infrastructure.components.binder.EventBinder;
import com.tom.meeter.infrastructure.components.downloader.EventImageDownloader;
import com.tom.meeter.infrastructure.components.downloader.UserWithCacheDownloader;

import javax.inject.Inject;

public class EventBinderImpl implements EventBinder<EventViewHolder> {

    private static final String TAG = EventBinderImpl.class.getCanonicalName();

    private final EventImageDownloader eventImageDownloader;
    private final UserWithCacheDownloader userDownloader;

    private Context ctx;
    private OnEventClickListener listener;

    @Inject
    public EventBinderImpl(
          EventImageDownloader eventImageDownloader,
          UserWithCacheDownloader userDownloader) {
        this.eventImageDownloader = eventImageDownloader;
        this.userDownloader = userDownloader;
    }

    @Override
    public void setupOnEventClickListener(
          OnEventClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void setContext(Context ctx) {
        this.ctx = ctx;
        eventImageDownloader.setContext(ctx);
        userDownloader.setContext(ctx);
    }

    @Override
    public void setOnAuthFailAction(Runnable onAuthFail) {
        eventImageDownloader.setOnAuthFailAction(onAuthFail);
        userDownloader.setOnAuthFailAction(onAuthFail);
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
                  AuthHelper.getAuthHeader(AccountManager.get(ctx)),
                  event.getCreatorId(),
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
