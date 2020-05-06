package com.tobiasdiez.easybind.select;

import java.util.function.Function;

import javafx.beans.value.ObservableValue;

import com.tobiasdiez.easybind.monadic.MonadicBinding;

public interface SelectBuilder<T> {
    <U> SelectBuilder<U> select(Function<? super T, ObservableValue<U>> selector);
    <U> MonadicBinding<U> selectObject(Function<? super T, ObservableValue<U>> selector);

    static <T> SelectBuilder<T> startAt(ObservableValue<T> selectionRoot) {
        return new RootSelectedBuilder<T>(selectionRoot);
    }
}
