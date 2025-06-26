package com.tom.meeter.context.profile.factory;

import android.content.Context;

import androidx.lifecycle.ViewModel;

public interface AssistedFactoryBase<T extends ViewModel> {

    T create(String auth, Context ctx, Runnable onNotAuthenticated);
}
