package com.tobiasdiez.easybind.select;

import java.util.function.Function;
import javafx.beans.value.ObservableValue;

import com.tobiasdiez.easybind.EasyBinding;

public interface SelectBuilder<T> {
    static <T> SelectBuilder<T> startAt(ObservableValue<T> selectionRoot) {
        return new RootSelectedBuilder<T>(selectionRoot);
    }

    <U> SelectBuilder<U> select(Function<? super T, ObservableValue<U>> selector);

    <U> EasyBinding<U> selectObject(Function<? super T, ObservableValue<U>> selector);
}
