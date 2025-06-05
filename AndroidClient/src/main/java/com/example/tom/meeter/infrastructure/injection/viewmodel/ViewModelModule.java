package com.example.tom.meeter.infrastructure.injection.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.example.tom.meeter.context.profile.viewmodel.UserEventsViewModel;
import com.example.tom.meeter.context.profile.viewmodel.ProfileEventsViewModel;
import com.example.tom.meeter.context.profile.viewmodel.ProfileViewModel;
import com.example.tom.meeter.context.profile.viewmodel.UserProfileViewModel;

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

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel profileViewModel(ProfileViewModel profileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileEventsViewModel.class)
    abstract ViewModel profileEventsViewModel(ProfileEventsViewModel profileEventsViewModel);

}
