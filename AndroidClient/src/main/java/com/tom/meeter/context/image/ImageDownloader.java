package com.tom.meeter.context.image;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.peekToken;

import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import com.tom.meeter.infrastructure.common.Globals;
import com.tom.meeter.infrastructure.http.DisconnectLogger;
import com.tom.meeter.infrastructure.http.HttpCodes;

import java.util.function.Consumer;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ImageDownloader {

    private static final String TAG = ImageDownloader.class.getCanonicalName();
    private final ImageService imageService;

    @Inject
    public ImageDownloader(ImageService imageService) {
        this.imageService = imageService;
    }

    public void downloadEventImage(
          String photoPath, Context ctx,
          Consumer<ResponseBody> onDownloaded, Runnable onNotAuthenticated) {
        if (photoPath == null) {
            return;
        }
        imageService.downloadEventImage(
                    Globals.getAuthHeader(peekToken(AccountManager.get(ctx))), photoPath)
              .enqueue(new DisconnectLogger<>(ctx) {
                  @Override
                  public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                      ResponseBody body = response.body();
                      if (response.code() == HttpCodes.OK && body != null) {
                          onDownloaded.accept(response.body());
                          return;
                      }
                      if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                          onNotAuthenticated.run();
                      }
                      Log.i(TAG, "/images/event/: " + response.code() + " : " + body);
                  }
              });
    }

    public void downloadUserImage(
          String photoPath, Context ctx,
          Consumer<ResponseBody> onDownloaded, Runnable onNotAuthenticated) {
        if (photoPath == null) {
            return;
        }
        imageService.downloadUserImage(
                    Globals.getAuthHeader(peekToken(AccountManager.get(ctx))), photoPath)
              .enqueue(new DisconnectLogger<>(ctx) {
                  @Override
                  public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                      ResponseBody body = response.body();
                      if (response.code() == HttpCodes.OK && body != null) {
                          onDownloaded.accept(response.body());
                          return;
                      }
                      if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                          onNotAuthenticated.run();
                      }
                      Log.i(TAG, "/images/user/: " + response.code() + " : " + body);
                  }
              });
    }
}
