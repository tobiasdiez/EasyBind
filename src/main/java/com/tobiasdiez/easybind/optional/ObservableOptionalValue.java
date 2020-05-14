package com.tobiasdiez.easybind.optional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;

import com.tobiasdiez.easybind.EasyBind;
import com.tobiasdiez.easybind.EasyBinding;
import com.tobiasdiez.easybind.EasyObservableValue;
import com.tobiasdiez.easybind.SimpleChangeListener;
import com.tobiasdiez.easybind.Subscription;

/**
 * An {@link ObservableValue} that may or may not contain values (i.e. an {@link Optional} in the category of observable values).
 */
public interface ObservableOptionalValue<T> extends ObservableObjectValue<Optional<T>> {

    /**
     * Checks whether this observable holds a value.
     *
     * @return {@code true} if a value is present, otherwise {@code false}
     */
    default boolean isValuePresent() {
        return getValue().isPresent();
    }

    /**
     * Checks whether this observable has no value.
     *
     * @return {@code true} if a value is not present, otherwise {@code false}
     */
    default boolean isValueEmpty() {
        return !isValuePresent();
    }

    /**
     * If this observable holds a value, invokes the given function with the value, otherwise does nothing.
     *
     * @param action the function to invoke on the value currently held by this observable
     */
    default void ifValuePresent(Consumer<? super T> action) {
        getValue().ifPresent(action);
    }

    /**
     * If this observable holds a value, returns the value, otherwise throws {@link NoSuchElementException}.
     *
     * @throws NoSuchElementException if no is value present
     */
    default T getValueOrElseThrow() {
        return getValue().orElseThrow(() -> new NoSuchElementException("No value present"));
    }

    /**
     * If this observable holds a value, returns the value, otherwise returns {@code other}.
     *
     * @param other value to return if there is no value present in this observable
     * @return the value, if present, otherwise {@code other}.
     */
    default T getValueOrElse(T other) {
        return getValue().orElse(other);
    }

    /**
     * Converts this optional observable to an ordinary observable, holding {@code null} if this observable does not hold any value.
     *
     * @return an observable that has the same value as this observable, if present, otherwise {@code null}.
     */
    default EasyObservableValue<T> asOrdinary() {
        return orElse((T) null);
    }

    /**
     * Returns a new observable that holds the value held by this observable, or {@code other} when this observable is empty.
     */
    EasyBinding<T> orElse(T other);

    /**
     * Returns a new observable that holds the value held by this observable, or the value held by {@code other} when this observable is empty.
     */
    OptionalBinding<T> orElse(ObservableValue<T> other);

    /**
     * Returns a new observable that holds the same value as this observable when the value is present and matches the given predicate,
     * otherwise empty.
     *
     * @param predicate the predicate to apply to a value, if present
     * @throws NullPointerException if the predicate is {@code null}
     */
    OptionalBinding<T> filter(Predicate<? super T> predicate);

    /**
     * Returns a new observable that holds the result of applying the given function to the value as this observable, if present, otherwise empty.
     * If the function returns {@code null}, then this is converted to an empty optional.
     *
     * @param mapper the mapping to apply to a value, if present
     * @see EasyBind#map(ObservableValue, Function)
     */
    <U> OptionalBinding<U> map(Function<? super T, ? extends U> mapper);

    /**
     * Returns a new observable that holds the value of the observable resulting from applying the given function to the value as this observable.
     * If this observable is empty or the function returns {@code null}, then this is converted to an empty optional.
     *
     * @param mapper the mapping to apply to a value, if present
     * @see EasyBind#mapObservable(ObservableValue, Function)
     */
    default <U, O extends ObservableValue<U>> OptionalBinding<U> mapObservable(Function<? super T, O> mapper) {
        // We will reuse the existing mapping method. For this we need to convert to optionals and back
        // TODO: This is ugly, better way would be to implement a FlatMapBinding specific to optional values
        Function<Optional<T>, ? extends ObservableValue<U>> mapperOpt = optionalVal -> {
            Optional<O> newObservable = optionalVal.map(mapper);
            if (newObservable.isPresent()) {
                return newObservable.get();
            } else {
                return new ReadOnlyObjectWrapper<>(null);
            }
        };
        EasyBinding<U> binding = EasyBind.mapObservable(this, mapperOpt);
        return new OptionalWrapper<>(binding);
    }

    /**
     * @see EasyBind#selectProperty(ObservableValue, Function)
     * @see #mapObservable(Function)
     */
    default <U, O extends Property<U>> PropertyBinding<U> selectProperty(Function<? super T, O> mapper) {
        // We will reuse the existing mapping method. For this we need to convert to optionals and back
        // TODO: This is ugly, better way would be to implement a FlatMapBinding specific to optional values
        Function<Optional<T>, ? extends Property<U>> mapperOpt = optionalVal -> {
            Optional<O> newObs = optionalVal.map(mapper);
            if (newObs.isPresent()) {
                return newObs.get();
            } else {
                return new ReadOnlyObjectWrapper<U>(null);
            }
        };
        return EasyBind.selectProperty(this, mapperOpt);
    }

    /**
     * Adds a change listener and returns a {@link Subscription} that can be used to remove that listener.
     * The listener will only be invoked if the new value is present, use {@link #listen(ChangeListener)} if you want to be notified about empty values as well.
     *
     * @see EasyBind#listen(ObservableValue, ChangeListener)
     */
    default Subscription listenToValues(SimpleChangeListener<? super T> listener) {
        ChangeListener<Optional<T>> listenerOpt = (observable, oldValue, newValue) -> newValue.ifPresent(newVal -> listener.changed(oldValue.orElse(null), newVal));
        return listen(listenerOpt);
    }

    /**
     * Invokes the {@code subscriber} for the current value, if present, and every new value.
     * Use {@link #subscribe(Consumer)} if you want to be notified about empty values as well.
     *
     * @param subscriber action to invoke for values of this observable.
     * @return a subscription that can be used to stop invoking subscriber for any further changes.
     * @see EasyBind#subscribe(ObservableValue, Consumer)
     */
    default Subscription subscribeToValues(Consumer<? super T> subscriber) {
        Consumer<Optional<T>> subscriberOpt = newValue -> newValue.ifPresent(subscriber);
        return EasyBind.subscribe(this, subscriberOpt);
    }

    /**
     * @see EasyBind#subscribe(ObservableValue, Consumer)
     */
    default Subscription subscribe(Consumer<? super Optional<T>> listener) {
        return EasyBind.subscribe(this, listener);
    }

    /**
     * @see EasyBind#listen(ObservableValue, ChangeListener)
     */
    default Subscription listen(ChangeListener<? super Optional<T>> listener) {
        return EasyBind.listen(this, listener);
    }
}
