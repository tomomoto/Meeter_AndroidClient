package com.tom.meeter.infrastructure.injection.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.util.Log;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static final String TAG = ViewModelFactory.class.getCanonicalName();
    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> viewModels;

    @Inject
    public ViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> viewModels) {
        logMethod(TAG, this);
        this.viewModels = viewModels;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        Log.d(TAG, "ViewModelFactory creates " + modelClass);

        Provider<ViewModel> viewModelProvider = viewModels.get(modelClass);

        if (viewModelProvider == null) {
            throw new IllegalArgumentException("ViewModel class " + modelClass
                  + " not found. Check " + ViewModelModule.class.getCanonicalName()
                  + " file to be properly aligned.");
        }

        return (T) viewModelProvider.get();
    }
}
