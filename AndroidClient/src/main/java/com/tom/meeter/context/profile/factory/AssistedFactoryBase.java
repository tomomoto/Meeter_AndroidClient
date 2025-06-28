package com.tom.meeter.context.profile.factory;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public interface AssistedFactoryBase<T extends ViewModel> {

    T create(Context ctx, Runnable onNotAuthenticated);

    default ViewModelProvider.Factory factory(
          AssistedFactoryBase<T> assistedFactory, Context ctx,
          Runnable onNotAuthenticated) {
        return new ViewModelProvider.Factory() {
            @Override
            @SuppressWarnings("unchecked")
            public <R extends ViewModel> R create(Class<R> modelClass) {
                T result = assistedFactory.create(ctx, onNotAuthenticated);
                if (modelClass.isInstance(result)) {
                    return (R) result;
                }
                throw new IllegalArgumentException(
                      "Unknown ViewModel class: " + modelClass.getName());
            }
        };
    }
}
