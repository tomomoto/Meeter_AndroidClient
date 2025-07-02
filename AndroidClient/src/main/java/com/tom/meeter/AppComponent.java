package com.tom.meeter;

import android.app.Application;

import com.tom.meeter.context.image.ImageDownloader;
import com.tom.meeter.context.image.activity.BaseUploadActivity;
import com.tom.meeter.context.launcher.Launcher;
import com.tom.meeter.context.token.service.TokenService;
import com.tom.meeter.infrastructure.components.UserLoader;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Component(
      modules = {
            AppModule.class,
            TokenModule.class,
            ImageModule.class
      })
@Singleton
public interface AppComponent {

    TokenService provideTokenService();

    ImageDownloader provideImageDownloader();

    UserLoader provideUserLoader();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(Launcher launcher);

    void inject(BaseUploadActivity baseUploadActivity);

}
