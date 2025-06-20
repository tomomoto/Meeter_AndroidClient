package com.tom.meeter.context.event.viewmodel;

import androidx.lifecycle.ViewModel;

import com.tom.meeter.infrastructure.injection.viewmodel.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class EventViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(EventViewModel.class)
    abstract ViewModel eventViewModel(EventViewModel eventViewModel);
}
