package com.tom.meeter.context.user;

import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.user.viewmodel.UserSubscribersViewModel;
import com.tom.meeter.context.user.viewmodel.UserSubscriptionsViewModel;
import com.tom.meeter.context.user.viewmodel.UserViewModel;
import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class UserViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel.class)
    abstract ViewModel userViewModel(UserViewModel userViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserSubscribersViewModel.class)
    abstract ViewModel userSubscribersViewModel(UserSubscribersViewModel usvm);

    @Binds
    @IntoMap
    @ViewModelKey(UserSubscriptionsViewModel.class)
    abstract ViewModel userSubscriptionsViewModel(UserSubscriptionsViewModel usvm);
}
