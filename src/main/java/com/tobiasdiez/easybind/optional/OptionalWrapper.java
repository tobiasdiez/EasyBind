package com.tobiasdiez.easybind.optional;

import java.util.Optional;
import javafx.beans.value.ObservableValue;

import com.tobiasdiez.easybind.EasyBind;
import com.tobiasdiez.easybind.EasyObservableValue;

/**
 * Provides a wrapper around the given observable to convert it to an {@link ObservableOptionalValue}.
 */
public class OptionalWrapper<T> extends PreboundOptionalBinding<T> {
    private final ObservableValue<T> value;

    public OptionalWrapper(ObservableValue<T> value) {
        super(value);
        this.value = value;
    }

    @Override
    protected Optional<T> computeValue() {
        return Optional.ofNullable(value.getValue());
    }

    /**
     * implNote overwrite to remove this wrapper from the notification chain
     */
    @Override
    public EasyObservableValue<T> asOrdinary() {
        return EasyBind.wrap(value);
    }
}
