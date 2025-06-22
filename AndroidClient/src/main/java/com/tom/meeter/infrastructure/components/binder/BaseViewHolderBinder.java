package com.tom.meeter.infrastructure.components.binder;

public interface BaseViewHolderBinder<VH, TARGET> {

    void bind(VH holder, TARGET target);
}
