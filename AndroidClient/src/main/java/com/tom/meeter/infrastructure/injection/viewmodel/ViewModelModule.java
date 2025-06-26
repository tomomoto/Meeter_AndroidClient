package com.tom.meeter.infrastructure.injection.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.profile.viewmodel.ProfileEventsViewModel;
import com.tom.meeter.context.profile.viewmodel.ProfileSubscribersViewModel;
import com.tom.meeter.context.profile.viewmodel.ProfileSubscriptionsViewModel;
import com.tom.meeter.context.profile.viewmodel.ProfileViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    private static final String TAG = ViewModelModule.class.getCanonicalName();

    public ViewModelModule() {
        Log.d(TAG, "Configuring ViewModelModule...");
    }

/*    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel profileViewModel(ProfileViewModel profileViewModel);*/

/*
    @Binds
    @IntoMap
    @ViewModelKey(ProfileEventsViewModel.class)
    abstract ViewModel profileEventsViewModel(ProfileEventsViewModel profileEventsViewModel);
*/

    @Binds
    @IntoMap
    @ViewModelKey(ProfileSubscribersViewModel.class)
    abstract ViewModel profileSubscribersViewModel(ProfileSubscribersViewModel psvm);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileSubscriptionsViewModel.class)
    abstract ViewModel profileSubscriptionsViewModel(ProfileSubscriptionsViewModel psvm);
}
