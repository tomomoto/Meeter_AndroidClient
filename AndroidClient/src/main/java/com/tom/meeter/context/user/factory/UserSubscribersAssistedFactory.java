package com.tom.meeter.context.user.factory;

import com.tom.meeter.context.user.viewmodel.UserSubscribersViewModel;
import com.tom.meeter.infrastructure.factory.AssistedFactoryWithIdBase;

import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface UserSubscribersAssistedFactory
      extends AssistedFactoryWithIdBase<UserSubscribersViewModel> {
}
