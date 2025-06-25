package com.tom.meeter.context.user;

import android.app.Application;

import com.tom.meeter.AppComponent;
import com.tom.meeter.context.user.activity.UserActivity;
import com.tom.meeter.context.user.activity.UserSubscribersActivity;
import com.tom.meeter.context.user.activity.UserSubscriptionsActivity;
import com.tom.meeter.context.user.viewmodel.UserViewModelModule;

import dagger.BindsInstance;
import dagger.Component;

@UserScope
@Component(
      modules = {UserViewModelModule.class},
      dependencies = {AppComponent.class})
public interface UserComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        Builder appComponent(AppComponent appComponent);

        UserComponent build();
    }

    void inject(UserActivity userActivity);

    void inject(UserSubscribersActivity usa);

    void inject(UserSubscriptionsActivity usa);
}
