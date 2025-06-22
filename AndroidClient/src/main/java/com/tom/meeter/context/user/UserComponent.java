package com.tom.meeter.context.user;

import android.app.Application;

import com.tom.meeter.context.image.ImageComponent;
import com.tom.meeter.context.token.TokenComponent;
import com.tom.meeter.context.user.activity.UserActivity;
import com.tom.meeter.context.user.activity.UserSubscribersActivity;
import com.tom.meeter.context.user.activity.UserSubscriptionsActivity;
import com.tom.meeter.context.user.service.UserService;
import com.tom.meeter.context.user.viewmodel.UserViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@UserScope
@Component(
      modules = {
            UserModule.class,
            UserViewModelModule.class
      },
      dependencies = {
            TokenComponent.class,
            ImageComponent.class
      })
public interface UserComponent {

    UserService provideUserService();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        Builder tokenComponent(TokenComponent tokenComponent);

        Builder imageComponent(ImageComponent tokenComponent);

        UserComponent build();
    }

    void inject(UserActivity userActivity);

    void inject(UserSubscribersActivity usa);

    void inject(UserSubscriptionsActivity usa);
}
