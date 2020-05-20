package com.tobiasdiez.easybind;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import com.tobiasdiez.easybind.optional.ObservableOptionalValue;
import com.tobiasdiez.easybind.optional.OptionalBinding;
import com.tobiasdiez.easybind.optional.OptionalWrapper;
import com.tobiasdiez.easybind.optional.PreboundOptionalBinding;
import com.tobiasdiez.easybind.optional.PropertyBinding;
import com.tobiasdiez.easybind.select.SelectBuilder;

/**
 * Methods for easy creation of bindings.
 */
public class EasyBind {

    /**
     * Creates a wrapper around the given observable to provide access to convenient helper methods (for fluent style)
     *
     * @param value the observable to wrap
     * @return a thin wrapper around the given observable
     */
    public static <T> EasyObservableValue<T> wrap(ObservableValue<T> value) {
        return new EasyObservableValue<T>() {
            @Override
            public T get() {
                return value.getValue();
            }

            @Override
            public void addListener(ChangeListener<? super T> listener) {
                value.addListener(listener);
            }

            @Override
            public void removeListener(ChangeListener<? super T> listener) {
                value.removeListener(listener);
            }

            @Override
            public T getValue() {
                return value.getValue();
            }

            @Override
            public void addListener(InvalidationListener listener) {
                value.addListener(listener);
            }

            @Override
            public void removeListener(InvalidationListener listener) {
                value.removeListener(listener);
            }
        };
    }

    /**
     * Creates a wrapper around the given observable value that provides functionality similar to an {@link Optional}.
     *
     * @param value the observable to wrap
     * @return a thin wrapper around the given observable
     */
    public static <T> ObservableOptionalValue<T> wrapNullable(ObservableValue<T> value) {
        return new OptionalWrapper<>(value);
    }

    /**
     * Creates a wrapper around the given observable list to provide access to convenient helper methods (for fluent style)
     *
     * @param list the observable list to wrap
     * @return a thin wrapper around the given observable list
     */
    public static <E> EasyObservableList<E> wrapList(ObservableList<E> list) {
        return new EasyObservableList<E>() {
            public int size() {
                return list.size();
            }

            public boolean isEmpty() {
                return list.isEmpty();
            }

            public boolean contains(Object o) {
                return list.contains(o);
            }

            public Iterator<E> iterator() {
                return list.iterator();
            }

            public Object[] toArray() {
                return list.toArray();
            }

            public <T> T[] toArray(T[] a) {
                return list.toArray(a);
            }

            public boolean add(E e) {
                return list.add(e);
            }

            public boolean remove(Object o) {
                return list.remove(o);
            }

            public boolean containsAll(Collection<?> c) {
                return list.containsAll(c);
            }

            public boolean addAll(Collection<? extends E> c) {
                return list.addAll(c);
            }

            public boolean addAll(int index, Collection<? extends E> c) {
                return list.addAll(index, c);
            }

            public boolean removeAll(Collection<?> c) {
                return list.removeAll(c);
            }

            public boolean retainAll(Collection<?> c) {
                return list.retainAll(c);
            }

            public void clear() {
                list.clear();
            }

            @Override
            public boolean equals(Object o) {
                return list.equals(o);
            }

            @Override
            public int hashCode() {
                return list.hashCode();
            }

            public E get(int index) {
                return list.get(index);
            }

            public E set(int index, E element) {
                return list.set(index, element);
            }

            public void add(int index, E element) {
                list.add(index, element);
            }

            public E remove(int index) {
                return list.remove(index);
            }

            public int indexOf(Object o) {
                return list.indexOf(o);
            }

            public int lastIndexOf(Object o) {
                return list.lastIndexOf(o);
            }

            public ListIterator<E> listIterator() {
                return list.listIterator();
            }

            public ListIterator<E> listIterator(int index) {
                return list.listIterator(index);
            }

            public List<E> subList(int fromIndex, int toIndex) {
                return list.subList(fromIndex, toIndex);
            }

            public void addListener(InvalidationListener listener) {
                list.addListener(listener);
            }

            public void removeListener(InvalidationListener listener) {
                list.removeListener(listener);
            }

            public void addListener(ListChangeListener<? super E> listener) {
                list.addListener(listener);
            }

            public void removeListener(ListChangeListener<? super E> listener) {
                list.removeListener(listener);
            }

            public boolean addAll(E... elements) {
                return list.addAll(elements);
            }

            public boolean setAll(E... elements) {
                return list.setAll(elements);
            }

            public boolean setAll(Collection<? extends E> col) {
                return list.setAll(col);
            }

            public boolean removeAll(E... elements) {
                return list.removeAll(elements);
            }

            public boolean retainAll(E... elements) {
                return list.retainAll(elements);
            }

            public void remove(int from, int to) {
                list.remove(from, to);
            }
        };
    }

    /**
     * Returns an observable consisting of the result of applying the given function to the given observable value.
     * <p>
     * The value passed to the {@code mapper} may be {@code null}. If this is not desired, use {@code wrapNullable(source).map(mapper)} instead.
     *
     * @param source the original observable value serving as input
     * @param mapper the function to apply
     * @return the new observable value
     * @see #mapObservable(ObservableValue, Function)
     */
    public static <T, U> EasyBinding<U> map(ObservableValue<T> source, Function<? super T, ? extends U> mapper) {
        return new EasyPreboundBinding<U>(source) {
            @Override
            protected U computeValue() {
                return mapper.apply(source.getValue());
            }
        };
    }

    /**
     * Returns an observable that, when the given observable value holds value {@code x}, holds the value held by the observable {@code f(x)}.
     *
     * @param source the original observable value serving as input
     * @param mapper the function to apply, returning an observable value
     * @see #map(ObservableValue, Function)
     */
    public static <T, O, R extends ObservableValue<O>> EasyBinding<O> mapObservable(ObservableValue<T> source, Function<? super T, R> mapper) {
        return new FlatMapBinding<>(source, mapper);
    }

    /**
     * Similar to {@link #mapObservable(ObservableValue, Function)}, except the returned binding is
     * also a property. This means you can call {@code setValue()} and
     * {@code bind()} methods on the returned value, which delegates to the
     * currently selected Property.
     * <p>
     * As the value of this ObservableValue changes, so does the selected
     * Property. When the Property returned from this method is bound, as the
     * selected Property changes, the previously selected property is unbound
     * and the newly selected property is bound.
     *
     * <p>Note that if the currently selected property is {@code null}, then
     * calling {@code getValue()} on the returned value will return {@code null}
     * regardless of any prior call to {@code setValue()} or {@code bind()}.
     *
     * <p>Note that you need to retain a reference to the returned value to
     * prevent it from being garbage collected.
     */
    public static <T, U> PropertyBinding<U> selectProperty(ObservableValue<T> source, Function<? super T, ? extends Property<U>> mapper) {
        return new FlatMapProperty<>(source, mapper);
    }

    public static <T, U> EasyObservableList<U> map(ObservableList<? extends T> sourceList, Function<? super T, ? extends U> f) {
        return new MappedList<>(sourceList, f);
    }

    public static <T> ObservableList<T> flatten(ObservableList<ObservableList<? extends T>> sources) {
        return new FlattenedList<>(sources);
    }

    @SafeVarargs
    public static <T> ObservableList<T> concat(ObservableList<? extends T>... sources) {
        return new FlattenedList<>(FXCollections.observableArrayList(sources));
    }

    /**
     * Creates a new list in which each element is converted using the provided mapping.
     * All changes to the underlying list are propagated to the converted list.
     * <p>
     * In contrast to {@link #map(ObservableList, Function)},
     * the items are converted when the are inserted instead of when they are accessed.
     * Thus the initial CPU overhead and memory consumption is higher but the access to list items is quicker.
     */
    public static <A, B> EasyObservableList<B> mapBacked(ObservableList<A> source, Function<A, B> mapper) {
        return new MappedBackedList<>(source, mapper);
    }

    public static <A, B, R> EasyBinding<R> combine(ObservableValue<A> src1, ObservableValue<B> src2, BiFunction<A, B, R> f) {
        return new EasyPreboundBinding<R>(src1, src2) {
            @Override
            protected R computeValue() {
                return f.apply(src1.getValue(), src2.getValue());
            }
        };
    }

    public static <A, B, C, R> EasyBinding<R> combine(ObservableValue<A> src1, ObservableValue<B> src2, ObservableValue<C> src3, TriFunction<A, B, C, R> f) {
        return new EasyPreboundBinding<R>(src1, src2, src3) {
            @Override
            protected R computeValue() {
                return f.apply(src1.getValue(), src2.getValue(), src3.getValue());
            }
        };
    }

    public static <A, B, C, D, R> EasyBinding<R> combine(ObservableValue<A> src1, ObservableValue<B> src2, ObservableValue<C> src3, ObservableValue<D> src4, TetraFunction<A, B, C, D, R> f) {
        return new EasyPreboundBinding<R>(src1, src2, src3, src4) {
            @Override
            protected R computeValue() {
                return f.apply(src1.getValue(), src2.getValue(), src3.getValue(), src4.getValue());
            }
        };
    }

    public static <A, B, C, D, E, R> EasyBinding<R> combine(ObservableValue<A> src1, ObservableValue<B> src2, ObservableValue<C> src3, ObservableValue<D> src4, ObservableValue<E> src5, PentaFunction<A, B, C, D, E, R> f) {
        return new EasyPreboundBinding<R>(src1, src2, src3, src4, src5) {
            @Override
            protected R computeValue() {
                return f.apply(src1.getValue(), src2.getValue(), src3.getValue(), src4.getValue(), src5.getValue());
            }
        };
    }

    public static <A, B, C, D, E, F, R> EasyBinding<R> combine(ObservableValue<A> src1, ObservableValue<B> src2, ObservableValue<C> src3, ObservableValue<D> src4, ObservableValue<E> src5, ObservableValue<F> src6, HexaFunction<A, B, C, D, E, F, R> f) {
        return new EasyPreboundBinding<R>(src1, src2, src3, src4, src5, src6) {
            @Override
            protected R computeValue() {
                return f.apply(src1.getValue(), src2.getValue(), src3.getValue(), src4.getValue(), src5.getValue(), src6.getValue());
            }
        };
    }

    public static <T, R> EasyBinding<R> combine(ObservableList<? extends ObservableValue<? extends T>> list, Function<? super Stream<T>, ? extends R> f) {
        return new ListCombinationBinding<>(list, f);
    }


    /**
     * Creates a new binding that performs a reduction on the
     * elements of this list, using the provided accumulation function.
     *
     * @param list        the source list
     * @param accumulator the accumulation function to apply
     * @see Stream#reduce(Object, BinaryOperator)
     * @see Stream#reduce(BinaryOperator)
     * @see Stream#reduce(Object, BiFunction, BinaryOperator)
     */
    public static <T, R> EasyBinding<R> reduce(ObservableList<? extends T> list, Function<? super Stream<? extends T>, ? extends R> accumulator) {
        return new EasyPreboundBinding<R>(list) {
            @Override
            protected R computeValue() {
                return accumulator.apply(list.stream());
            }
        };
    }

    /**
     * Starts a selection chain. A selection chain is just a more efficient
     * equivalent to a chain of flatMaps.
     */
    public static <T> SelectBuilder<T> select(ObservableValue<T> selectionRoot) {
        return SelectBuilder.startAt(selectionRoot);
    }

    /**
     * Sets up automatic binding and unbinding of {@code target} to/from
     * {@code source}, based on the changing value of {@code condition}.
     * In other words, this method starts watching {@code condition} for
     * changes. When {@code condition} changes to {@code true}, {@code target}
     * is bound to {@code source}. When {@code condition} changes to
     * {@code false}, {@code target} is unbound. This keeps happening until
     * either {@code unsubscribe()} is called on the returned subscription,
     * or {@code target} is garbage collected.
     *
     * @param target    target of the conditional binding
     * @param source    source of the conditional binding
     * @param condition controls when to bind and unbind target to/from source
     * @return a subscription that can be used to dispose the conditional
     * binding set up by this method, i.e. stop observing {@code condition}
     * and unbind {@code target} from {@code source}.
     * @deprecated Since 1.0.2. Use {@code when(condition).bind(target, source)}
     * instead.
     */
    @Deprecated
    public static <T> Subscription bindConditionally(Property<T> target, ObservableValue<? extends T> source, ObservableValue<Boolean> condition) {
        return new ConditionalBinding<>(target, source, condition);
    }

    /**
     * Sync the content of the {@code target} list with the {@code source} list.
     *
     * @return a subscription that can be used to stop syncing the lists.
     */
    public static <T> Subscription listBind(List<? super T> target, ObservableList<? extends T> source) {
        target.clear();
        target.addAll(source);
        ListChangeListener<? super T> listener = change -> {
            while (change.next()) {
                int from = change.getFrom();
                int to = change.getTo();
                if (change.wasPermutated()) {
                    target.subList(from, to).clear();
                    target.addAll(from, source.subList(from, to));
                } else {
                    target.subList(from, from + change.getRemovedSize()).clear();
                    target.addAll(from, source.subList(from, from + change.getAddedSize()));
                }
            }
        };
        source.addListener(listener);
        return () -> source.removeListener(listener);
    }

    /**
     * Entry point for creating conditional bindings.
     */
    public static When when(ObservableValue<Boolean> condition) {
        return new When(condition);
    }

    /**
     * Adds {@code element} to {@code collection} when {@code condition} is
     * {@code true} and removes it from {@code collection} when
     * {@code condition} is {@code false}.
     *
     * @return a subscription that can be used to stop observing
     * {@code condition} and manipulating {@code collection}.
     */
    public static <T> Subscription includeWhen(Collection<T> collection, T element, ObservableValue<Boolean> condition) {
        return subscribe(condition, new Consumer<Boolean>() {
            private boolean included = false;

            @Override
            public void accept(Boolean value) {
                if (value && !included) {
                    included = collection.add(element);
                } else if (!value && included) {
                    collection.remove(element);
                    included = false;
                }
            }
        });
    }

    /**
     * Invokes {@code subscriber} for the current and every new value of
     * {@code observable}.
     *
     * @param observable observable value to subscribe to
     * @param subscriber action to invoke for values of {@code observable}.
     * @return a subscription that can be used to stop invoking subscriber
     * for any further {@code observable} changes.
     */
    public static <T> Subscription subscribe(ObservableValue<T> observable, Consumer<? super T> subscriber) {
        subscriber.accept(observable.getValue());
        ChangeListener<? super T> listener = (obs, oldValue, newValue) -> subscriber.accept(newValue);
        return listen(observable, listener);
    }

    /**
     * Adds an invalidation listener and returns a Subscription that can be
     * used to remove that listener.
     *
     * <pre>
     * {@code
     * Subscription s = observable.listen(obs -> doSomething());
     *
     * // later
     * s.unsubscribe();
     * }</pre>
     * <p>
     * is equivalent to
     *
     * <pre>
     * {@code
     * InvalidationListener l = obs -> doSomething();
     * observable.addListener(l);
     *
     * // later
     * observable.removeListener();
     * }</pre>
     */
    public static <T> Subscription listen(ObservableValue<T> observable, InvalidationListener listener) {
        observable.addListener(listener);
        return () -> observable.removeListener(listener);
    }

    /**
     * Adds a change listener and returns a Subscription that can be
     * used to remove that listener. See the example at
     * {@link #listen(ObservableValue, InvalidationListener)}.
     */
    public static <T> Subscription listen(ObservableValue<T> observable, ChangeListener<? super T> listener) {
        observable.addListener(listener);
        return () -> observable.removeListener(listener);
    }

    /**
     * Creates a new {@link OptionalBinding} that contains the element
     * of an {@link ObservableList} at the specified position.
     * The binding will be empty if the {@code index} points behind the {@code ObservableList} or to a {@code null} element.
     *
     * @param list  the {@code ObservableList}
     * @param index the position in the {@code List}
     * @return the new {@code OptionalBinding}
     * @throws NullPointerException     if the {@code ObservableList} is {@code null}
     * @throws IllegalArgumentException if (@code index &lt; 0)
     */
    public static <E> OptionalBinding<E> valueAt(final ObservableList<E> list, final int index) {
        if (list == null) {
            throw new NullPointerException("List cannot be null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be negative");
        }

        return new PreboundOptionalBinding<E>(list) {
            @Override
            protected Optional<E> computeValue() {
                try {
                    return Optional.ofNullable(list.get(index));
                } catch (IndexOutOfBoundsException ex) {
                    return Optional.empty();
                }
            }
        };
    }


    /**
     * Creates a new {@link OptionalBinding} that contains the mapping of a specific key
     * in an {@link ObservableMap}.
     * The binding will be empty if the {@code key} is not contained in the map or points to a {@code null} element.
     *
     * @param map the {@code ObservableMap}
     * @param key the key in the {@code Map}
     * @return the new {@code ObjectBinding}
     * @throws NullPointerException if the {@code ObservableMap} is {@code null}
     */
    public static <K, V> OptionalBinding<V> valueAt(final ObservableMap<K, V> map, final K key) {
        if (map == null) {
            throw new NullPointerException("Map cannot be null.");
        }

        return new PreboundOptionalBinding<V>(map) {
            @Override
            protected Optional<V> computeValue() {
                try {
                    return Optional.ofNullable(map.get(key));
                } catch (ClassCastException | NullPointerException ex) {
                    return Optional.empty();
                }
            }
        };
    }

    @FunctionalInterface
    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    @FunctionalInterface
    public interface TetraFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }

    @FunctionalInterface
    public interface PentaFunction<A, B, C, D, E, R> {
        R apply(A a, B b, C c, D d, E e);
    }

    @FunctionalInterface
    public interface HexaFunction<A, B, C, D, E, F, R> {
        R apply(A a, B b, C c, D d, E e, F f);
    }
}
