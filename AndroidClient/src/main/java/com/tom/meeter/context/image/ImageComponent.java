package com.tom.meeter.context.image;

import android.app.Application;

import com.tom.meeter.context.image.activity.BaseUploadActivity;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {ImageModule.class})
public interface ImageComponent {

    ImageDownloader provideImageDownloader();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ImageComponent build();
    }

    void inject(BaseUploadActivity baseUploadActivity);
}
