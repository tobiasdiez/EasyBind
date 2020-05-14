package com.tobiasdiez.easybind;

import javafx.beans.binding.Binding;

/**
 * A standard {@link Binding} enriched by convenient helper methods to generate bindings in a fluent style.
 */
public interface EasyBinding<T> extends Binding<T>, EasyObservableValue<T> {
}
