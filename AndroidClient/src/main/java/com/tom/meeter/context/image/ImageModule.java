package com.tom.meeter.context.image;

import static com.tom.meeter.infrastructure.common.Globals.getServerPath;
import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.app.Application;

import androidx.annotation.NonNull;

import com.tom.meeter.context.image.service.ImageService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class ImageModule {

    private static final String TAG = ImageModule.class.getCanonicalName();

    public ImageModule() {
        logMethod(TAG, this);
    }

    @Singleton
    @NonNull
    @Provides
    public ImageService provideImageService(Application app) {
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .build()
              .create(ImageService.class);
    }

    @Singleton
    @NonNull
    @Provides
    public ImageDownloader provideImageDownloader(ImageService imageService) {
        return new ImageDownloader(imageService);
    }
}
