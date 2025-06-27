package com.tom.meeter.context.event.factory;

import com.tom.meeter.context.event.viewmodel.EventViewModel;
import com.tom.meeter.infrastructure.factory.AssistedFactoryWithIdBase;

import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface EventAssistedFactory
      extends AssistedFactoryWithIdBase<EventViewModel> {
}