package com.tom.meeter.infrastructure.components;

import android.content.Context;
import android.util.Log;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.infrastructure.http.BaseOnNotAuthenticatedCallback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class UserWithCacheDownloader {

    private static final String TAG = PhotoWithCacheDownloader.class.getCanonicalName();

    private final UserService service;

    private Context ctx;
    private Runnable onAuthFail;

    protected final Map<String, UserDTO> cache = new ConcurrentHashMap<>();

    @Inject
    public UserWithCacheDownloader(UserService service) {
        this.service = service;
    }

    public void setup(Context ctx, Runnable onAuthFail) {
        this.ctx = ctx;
        this.onAuthFail = onAuthFail;
    }

    public UserDTO getUserCache(String userId) {
        return cache.get(userId);
    }

/*    public UserWithCacheDownloader(
          Context ctx, UserService service, Runnable onAuthFail) {
        this.ctx = ctx;
        this.service = service;
        this.onAuthFail = onAuthFail;
    }*/

    public void loadUser(
          String auth, String userId, Consumer<UserDTO> onUserReady) {
        UserDTO cached = cache.get(userId);
        if (cached != null) {
            onUserReady.accept(cached);
            return;
        }

        fetchUser(
              auth, userId,
              user -> {
                  cache.put(user.getId(), user);
                  onUserReady.accept(user);
                  Log.d(TAG, getClass().getSimpleName() +
                        ": user downloaded for [" + user.getId() + "], cache updated.");
              });
    }

    private void fetchUser(
          String auth, String userId, Consumer<UserDTO> onDownloaded) {
        service.getUser(auth, userId).enqueue(new BaseOnNotAuthenticatedCallback<>(ctx, onAuthFail) {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> resp) {
                super.onResponse(call, resp);
                if (!resp.isSuccessful() || resp.body() == null) {
                    return;
                }
                onDownloaded.accept(resp.body());
            }
        });
    }
}
