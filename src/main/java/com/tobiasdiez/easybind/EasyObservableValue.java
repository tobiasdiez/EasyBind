package com.tobiasdiez.easybind;

import java.util.function.Consumer;
import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;

import com.tobiasdiez.easybind.optional.PropertyBinding;
import com.tobiasdiez.easybind.select.SelectBuilder;

/**
 * A standard {@link ObservableObjectValue} enriched by convenient helper methods to generate bindings in a fluent style.
 */
public interface EasyObservableValue<T> extends ObservableObjectValue<T> {

    /**
     * @see EasyBind#map(ObservableValue, Function)
     */
    default <R> EasyBinding<R> map(Function<? super T, ? extends R> mapper) {
        return EasyBind.map(this, mapper);
    }

    /**
     * @see EasyBind#mapObservable(ObservableValue, Function)
     */
    default <R> EasyBinding<R> mapObservable(Function<? super T, ? extends ObservableValue<R>> mapper) {
        return EasyBind.mapObservable(this, mapper);
    }

    /**
     * @see EasyBind#selectProperty(ObservableValue, Function)
     */
    default <R> PropertyBinding<R> selectProperty(Function<? super T, ? extends Property<R>> mapper) {
        return EasyBind.selectProperty(this, mapper);
    }

    /**
     * Starts a selection chain. A selection chain is just a more efficient
     * equivalent to a chain of {@link #mapObservable(Function)}.
     */
    default <U> SelectBuilder<U> select(Function<? super T, ObservableValue<U>> selector) {
        return SelectBuilder.startAt(this).select(selector);
    }

    /**
     * @see EasyBind#listen(ObservableValue, InvalidationListener)
     */
    default Subscription listen(InvalidationListener listener) {
        return EasyBind.listen(this, listener);
    }

    /**
     * @see EasyBind#listen(ObservableValue, ChangeListener)
     */
    default Subscription listen(ChangeListener<? super T> listener) {
        return EasyBind.listen(this, listener);
    }

    /**
     * @see EasyBind#subscribe(ObservableValue, Consumer)
     */
    default Subscription subscribe(Consumer<? super T> subscriber) {
        return EasyBind.subscribe(this, subscriber);
    }
}
