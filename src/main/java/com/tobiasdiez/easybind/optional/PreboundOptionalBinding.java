package com.tobiasdiez.easybind.optional;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

import com.tobiasdiez.easybind.EasyBinding;
import com.tobiasdiez.easybind.EasyPreboundBinding;
import com.tobiasdiez.easybind.PreboundBinding;

/**
 * Object binding that binds to its dependencies on creation
 * and unbinds from them on dispose. If one of the registered dependencies becomes invalid, this
 * binding is marked as invalid.
 * To provide a concrete implementation of this class, the method {@link #computeValue()}
 * has to be implemented to calculate the value of this binding based on the current state of the dependencies.
 * This method is called when {@link #get()} is invoked for an invalid binding.
 */
public abstract class PreboundOptionalBinding<T> extends PreboundBinding<Optional<T>> implements OptionalBinding<T> {

    public PreboundOptionalBinding(Observable... dependencies) {
        super(dependencies);
    }

    @Override
    public <U> OptionalBinding<U> map(Function<? super T, ? extends U> mapper) {
        return new PreboundOptionalBinding<U>(dependencies) {

            @Override
            protected Optional<U> computeValue() {
                return PreboundOptionalBinding.this.getValue().map(mapper);
            }
        };
    }

    @Override
    public EasyBinding<T> orElse(T other) {
        return new EasyPreboundBinding<T>(dependencies) {
            @Override
            protected T computeValue() {
                return PreboundOptionalBinding.this.getValue().orElse(other);
            }
        };
    }

    @Override
    public OptionalBinding<T> orElse(ObservableValue<T> other) {
        return new FirstNonNullBinding<>(this.asOrdinary(), other);
    }

    @Override
    public OptionalBinding<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return new PreboundOptionalBinding<T>(dependencies) {
            @Override
            protected Optional<T> computeValue() {
                return this.getValue().filter(predicate);
            }
        };
    }
}
