package com.tom.meeter.context.user.factory;

import android.content.Context;

import com.tom.meeter.context.user.viewmodel.UserSubscribersViewModel;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface UserSubscribersViewModelAssistedFactory
      extends AssistedFactoryBase<UserSubscribersViewModel> {

    @Override
    UserSubscribersViewModel create(
          @Assisted(ASSISTED_AUTH) String auth,
          @Assisted(ASSISTED_USER_ID) String userId,
          Context ctx,
          Runnable onNotAuthenticated);
}
