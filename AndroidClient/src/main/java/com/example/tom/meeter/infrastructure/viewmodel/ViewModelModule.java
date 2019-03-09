package com.example.tom.meeter.infrastructure.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.example.tom.meeter.context.event.UserEventsViewModel;
import com.example.tom.meeter.context.user.UserProfileViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(UserProfileViewModel.class)
    abstract ViewModel userProfileViewModel(UserProfileViewModel userProfileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserEventsViewModel.class)
    abstract ViewModel eventViewModel(UserEventsViewModel userEventsViewModel);

}
