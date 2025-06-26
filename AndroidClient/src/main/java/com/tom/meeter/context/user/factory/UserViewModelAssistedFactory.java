package com.tom.meeter.context.user.factory;

import android.content.Context;

import com.tom.meeter.context.user.viewmodel.UserViewModel;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface UserViewModelAssistedFactory
      extends AssistedFactoryBase<UserViewModel> {

    @Override
    UserViewModel create(
          @Assisted(ASSISTED_AUTH) String auth,
          @Assisted(ASSISTED_USER_ID) String userId,
          Context ctx,
          Runnable onNotAuthenticated);
}
