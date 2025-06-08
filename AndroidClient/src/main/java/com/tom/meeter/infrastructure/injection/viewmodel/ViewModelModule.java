package com.tom.meeter.infrastructure.injection.viewmodel;

import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.profile.viewmodel.ProfileEventsViewModel;
import com.tom.meeter.context.profile.viewmodel.ProfileViewModel;
import com.tom.meeter.context.profile.viewmodel.UserEventsViewModel;
import com.tom.meeter.context.profile.viewmodel.UserProfileViewModel;

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
