package com.tom.meeter.context.user.factory;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public interface AssistedFactoryBase<T extends ViewModel> {

    String ASSISTED_AUTH = "assisted_auth";
    String ASSISTED_USER_ID = "assisted_user_id";

    T create(String auth, String userId, Context ctx, Runnable onNotAuthenticated);

    default <F extends AssistedFactoryBase<T>> ViewModelProvider.Factory factory(
          F assistedFactory,String auth, String userId, Context ctx,
          Runnable onNotAuthenticated) {
        return new ViewModelProvider.Factory() {
            @Override
            @SuppressWarnings("unchecked")
            //TODO associate T2 with T?
            public <T2 extends ViewModel> T2 create(Class<T2> modelClass) {
                return (T2) assistedFactory.create(auth, userId, ctx, onNotAuthenticated);
            }
        };
    }
}
