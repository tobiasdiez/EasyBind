package com.tobiasdiez.easybind;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Simple {@link ChangeListener} that only notifies about the old and new value but not about the changed observable.
 * Useful if you register listeners to a specific observable, so that the observable is clear from the context.
 */
@FunctionalInterface
public interface SimpleChangeListener<T> extends ChangeListener<T> {
    /**
     * Called when the value of an observable changes.
     * <p>
     * In general, it is considered bad practice to modify the observed value in this method.
     *
     * @param oldValue the old value
     * @param newValue the new value
     */
    void changed(T oldValue, T newValue);

    default void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        changed(oldValue, newValue);
    }
}
