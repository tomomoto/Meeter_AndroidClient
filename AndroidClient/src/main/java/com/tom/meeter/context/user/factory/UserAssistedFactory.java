package com.tom.meeter.context.user.factory;

import com.tom.meeter.context.user.viewmodel.UserViewModel;
import com.tom.meeter.infrastructure.factory.AssistedFactoryWithIdBase;

import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface UserAssistedFactory
      extends AssistedFactoryWithIdBase<UserViewModel> {
}
