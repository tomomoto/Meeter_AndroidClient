package com.tom.meeter.context.user.factory;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public interface AssistedFactoryBase<T extends ViewModel> {

    T create(String userId, Context ctx, Runnable onNotAuthenticated);

    default ViewModelProvider.Factory factory(
          AssistedFactoryBase<T> assistedFactory,
          String userId, Context ctx,
          Runnable onNotAuthenticated) {
        return new ViewModelProvider.Factory() {
            @Override
            @SuppressWarnings("unchecked")
            public <C extends ViewModel> C create(Class<C> modelClass) {
                T result = assistedFactory.create(userId, ctx, onNotAuthenticated);
                if (modelClass.isInstance(result)) {
                    return (C) result;
                }
                throw new IllegalArgumentException(
                      "Unknown ViewModel class: " + modelClass.getName());
            }
        };
    }
}
