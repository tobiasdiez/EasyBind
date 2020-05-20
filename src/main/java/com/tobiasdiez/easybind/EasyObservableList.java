package com.tobiasdiez.easybind;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import com.tobiasdiez.easybind.optional.OptionalBinding;

/**
 * A standard {@link ObservableList} enriched by convenient helper methods.
 */
public interface EasyObservableList<E> extends ObservableList<E> {

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if any elements of this list match the provided
     * predicate. May not evaluate the predicate on all elements if not necessary for determining the result.
     * If the list is empty then {@code false} is returned and the predicate is not evaluated.
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                  <a href="package-summary.html#Statelessness">stateless</a>
     *                  predicate to apply to elements of this list
     * @return the new {@code BooleanBinding}
     * @see java.util.stream.Stream#anyMatch(Predicate)
     */
    default BooleanBinding anyMatch(Predicate<? super E> predicate) {
        return Bindings.createBooleanBinding(() -> stream().anyMatch(predicate), this);
    }

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if all elements of this list match the provided
     * predicate. May not evaluate the predicate on all elements if not necessary for determining the result.
     * If the list is empty then {@code true} is returned and the predicate is not evaluated.
     *
     * @param predicate a <a href="package-summary.html#NonInterference">non-interfering</a>,
     *                  <a href="package-summary.html#Statelessness">stateless</a>
     *                  predicate to apply to elements of this list
     * @return the new {@code BooleanBinding}
     * @see java.util.stream.Stream#allMatch(Predicate)
     */
    default BooleanBinding allMatch(Predicate<? super E> predicate) {
        return Bindings.createBooleanBinding(() -> this.stream().allMatch(predicate), this);
    }

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if this list is empty.
     *
     * @return the new {@code BooleanBinding}
     * @see Bindings#isEmpty(ObservableList)
     */
    default BooleanBinding isEmptyBinding() {
        return Bindings.isEmpty(this);
    }

    /**
     * Creates a {@link FilteredList} wrapper of this list using the specified predicate.
     *
     * @param predicate the predicate to use
     * @return new {@code FilteredList}
     * @see ObservableList#filtered(Predicate)
     */
    default FilteredList<E> filtered(ObservableValue<? extends Predicate<E>> predicate) {
        FilteredList<E> filteredList = new FilteredList<>(this);
        filteredList.predicateProperty().bind(predicate);
        return filteredList;
    }

    /**
     * @see EasyBind#valueAt(ObservableList, int)
     */
    default OptionalBinding<E> valueAt(int index) {
        return EasyBind.valueAt(this, index);
    }

    /**
     * @see EasyBind#reduce(ObservableList, Function)
     */
    default <R> EasyBinding<R> ag(Function<? super Stream<? extends E>, ? extends R> accumulator) {
        return EasyBind.reduce(this, accumulator);
    }
}
