package com.tobiasdiez.easybind.optional;

import java.util.Optional;

import javafx.beans.binding.Binding;

public interface OptionalBinding<T> extends Binding<Optional<T>>, ObservableOptionalValue<T> {
}
