package com.example.tom.meeter;


import android.app.Application;

import com.example.tom.meeter.context.activities.ProfileActivity;
import com.example.tom.meeter.context.fragments.ProfileFragment;
import com.example.tom.meeter.infrastructure.viewmodule.ViewModelModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {AppModule.class, ViewModelModule.class})
@Singleton
public interface AppComponent {

    @Component.Builder
    interface Builder {

        /*@BindsInstance
        Builder appModule(AppModule appModule);*/

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(ProfileActivity profileActivity);
    void inject(ProfileFragment profileFragment);
}