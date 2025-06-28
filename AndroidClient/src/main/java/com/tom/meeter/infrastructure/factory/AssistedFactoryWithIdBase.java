package com.tom.meeter.infrastructure.factory;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public interface AssistedFactoryWithIdBase<T extends ViewModel> {

    T create(String id, Context ctx, Runnable onNotAuthenticated);

    default ViewModelProvider.Factory factory(
          AssistedFactoryWithIdBase<T> assistedFactory,
          String id, Context ctx, Runnable onNotAuthenticated) {
        return new ViewModelProvider.Factory() {
            @Override
            @SuppressWarnings("unchecked")
            public <R extends ViewModel> R create(Class<R> modelClass) {
                T result = assistedFactory.create(id, ctx, onNotAuthenticated);
                if (modelClass.isInstance(result)) {
                    return (R) result;
                }
                throw new IllegalArgumentException(
                      "Unknown ViewModel class: " + modelClass.getName());
            }
        };
    }
}

