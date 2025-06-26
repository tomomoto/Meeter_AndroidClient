package com.tom.meeter.context.image;

import static com.tom.meeter.context.auth.infrastructure.AuthHelper.getAuthHeader;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.tom.meeter.context.image.service.ImageService;
import com.tom.meeter.infrastructure.http.HttpCodes;
import com.tom.meeter.infrastructure.http.HttpErrorLogger;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ImageDownloader {

    private static final String TAG = ImageDownloader.class.getCanonicalName();
    private final ImageService imageService;

    @Inject
    public ImageDownloader(ImageService imageService) {
        logMethod(TAG, this);
        this.imageService = imageService;
    }

    public void downloadEventImage(
          String photoPath, Context ctx,
          Consumer<Bitmap> onDownloaded, Function<ResponseBody, Bitmap> bodyConverter,
          Runnable onNotAuthenticated) {
        imageService.downloadEventImage(getAuthHeader(AccountManager.get(ctx)), photoPath)
              .enqueue(new HttpErrorLogger<>(ctx) {
                  @Override
                  public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                      super.onResponse(call, response);
                      //Log.d(TAG, "/images/event" + photoPath + " downloaded...");
                      if (response.code() == HttpCodes.OK) {
                          try (ResponseBody body = response.body()) {
                              onDownloaded.accept(bodyConverter.apply(body));
                              return;
                          }
                      }
                      if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                          onNotAuthenticated.run();
                      }
                  }
              });
    }

    @Deprecated
    public void downloadEventImage(
          String photoPath, Context ctx,
          Consumer<ResponseBody> onDownloaded, Runnable onNotAuthenticated) {
        imageService.downloadEventImage(getAuthHeader(AccountManager.get(ctx)), photoPath)
              .enqueue(new HttpErrorLogger<>(ctx) {
                  @Override
                  public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                      super.onResponse(call, response);
                      //Log.d(TAG, "/images/event" + photoPath + " downloaded...");
                      if (response.code() == HttpCodes.OK) {
                          try (ResponseBody body = response.body()) {
                              onDownloaded.accept(body);
                              return;
                          }
                      }
                      if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                          onNotAuthenticated.run();
                      }
                  }
              });
    }

    @Deprecated
    public void downloadUserImage(
          String photoPath, Context ctx,
          Consumer<ResponseBody> onDownloaded, Runnable onNotAuthenticated) {
        imageService.downloadUserImage(getAuthHeader(AccountManager.get(ctx)), photoPath)
              .enqueue(new HttpErrorLogger<>(ctx) {
                  @Override
                  public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                      super.onResponse(call, response);
                      if (response.code() == HttpCodes.OK) {
                          try (ResponseBody body = response.body()) {
                              onDownloaded.accept(body);
                              return;
                          }
                      }
                      if (response.code() == HttpCodes.NOT_AUTHENTICATED) {
                          onNotAuthenticated.run();
                      }
                  }
              });
    }
}
