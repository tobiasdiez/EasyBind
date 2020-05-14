package com.tobiasdiez.easybind;

import javafx.beans.Observable;

/**
 * A {@link PreboundBinding} that also implements {@link EasyBinding} in order to support fluent method chains.
 */
public abstract class EasyPreboundBinding<T> extends PreboundBinding<T> implements EasyBinding<T> {
    public EasyPreboundBinding(Observable... dependencies) {
        super(dependencies);
    }
}
