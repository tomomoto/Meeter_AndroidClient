package com.tom.meeter.context.user.factory;

import android.content.Context;

import com.tom.meeter.context.user.viewmodel.UserSubscriptionsViewModel;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface UserSubscriptionsViewModelAssistedFactory
      extends AssistedFactoryBase<UserSubscriptionsViewModel> {

    @Override
    UserSubscriptionsViewModel create(
          @Assisted(ASSISTED_AUTH) String auth,
          @Assisted(ASSISTED_USER_ID) String userId,
          Context ctx,
          Runnable onNotAuthenticated);
}
