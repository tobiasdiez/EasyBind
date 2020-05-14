package com.tobiasdiez.easybind.select;

import java.util.function.Function;
import javafx.beans.value.ObservableValue;

import com.tobiasdiez.easybind.EasyBinding;

class IntermediateSelectedBuilder<T, U> implements ParentSelectedBuilder<U> {
    private final ParentSelectedBuilder<T> parent;
    private final Function<? super T, ObservableValue<U>> selector;

    public IntermediateSelectedBuilder(ParentSelectedBuilder<T> parent, Function<? super T, ObservableValue<U>> selector) {
        this.parent = parent;
        this.selector = selector;
    }

    @Override
    public <V> EasyBinding<V> create(NestedSelectionElementFactory<U, V> nestedSelectionFactory) {
        NestedSelectionElementFactory<T, V> intermediateSelectionFactory = onInvalidation -> {
            return new IntermediateSelectionElement<T, U, V>(onInvalidation, selector, nestedSelectionFactory);
        };
        return parent.create(intermediateSelectionFactory);
    }

}
