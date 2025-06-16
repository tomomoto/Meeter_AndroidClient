package com.tom.meeter.infrastructure.injection.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.event.viewmodel.EventViewModel;
import com.tom.meeter.context.profile.viewmodel.ProfileEventsViewModel;
import com.tom.meeter.context.profile.viewmodel.ProfileViewModel;
import com.tom.meeter.context.profile.viewmodel.UserEventsViewModel;
import com.tom.meeter.context.profile.viewmodel.UserProfileViewModel;
import com.tom.meeter.context.user.viewmodel.UserViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    private static final String TAG = ViewModelModule.class.getCanonicalName();

    public ViewModelModule() {
        Log.d(TAG, "Configuring ViewModelModule...");
    }

    @Binds
    @IntoMap
    @ViewModelKey(UserProfileViewModel.class)
    abstract ViewModel userProfileViewModel(UserProfileViewModel userProfileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserEventsViewModel.class)
    abstract ViewModel userEventViewModel(UserEventsViewModel userEventsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel profileViewModel(ProfileViewModel profileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileEventsViewModel.class)
    abstract ViewModel profileEventsViewModel(ProfileEventsViewModel profileEventsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel.class)
    abstract ViewModel userViewModel(UserViewModel userViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EventViewModel.class)
    abstract ViewModel eventViewModel(EventViewModel eventViewModel);

}
