package com.tom.meeter.context.user.factory;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tom.meeter.context.user.UserScope;
import com.tom.meeter.context.user.viewmodel.UserViewModelModule;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

@UserScope
public class UserViewModelFactory implements ViewModelProvider.Factory {

    private static final String TAG = UserViewModelFactory.class.getCanonicalName();
    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> viewModels;

    @Inject
    public UserViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> viewModels) {
        logMethod(TAG, this);
        this.viewModels = viewModels;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        Log.d(TAG, "UserViewModelFactory creates " + modelClass);

        Provider<ViewModel> viewModelProvider = viewModels.get(modelClass);

        if (viewModelProvider == null) {
            throw new IllegalArgumentException("ViewModel class " + modelClass
                  + " not found. Check " + UserViewModelModule.class.getCanonicalName()
                  + " file to be properly aligned.");
        }
        Log.d(TAG, "Before viewModelProvider.get() " + modelClass);
        T t = (T) viewModelProvider.get();
        Log.d(TAG, "After viewModelProvider.get()" + modelClass);

        return t;
    }
}
