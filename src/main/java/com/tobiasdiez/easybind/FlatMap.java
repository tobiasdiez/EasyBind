package com.tobiasdiez.easybind;

import java.util.function.Function;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

import com.tobiasdiez.easybind.optional.PropertyBinding;

/**
 * Converts an observable holding a value of type {@code T} to an observable holding a value of type {@code U},
 * using a {@code mapper} function that extracts an {@code ObservableValue<U>} from the source object.
 * The value hold by this class is the value hold by the extracted observable.
 */
abstract class FlatMapBindingBase<T, U, O extends ObservableValue<U>> extends ObjectBinding<U> implements EasyBinding<U> {
    private final ObservableValue<T> source;
    private final Function<? super T, O> mapper;
    // need to retain strong reference to listeners, so that they don't get garbage collected
    private final InvalidationListener sourceListener = obs -> sourceInvalidated();
    private final InvalidationListener weakSourceListener = new WeakInvalidationListener(sourceListener);
    private final InvalidationListener mappedListener = obs -> mappedInvalidated();
    private final InvalidationListener weakMappedListener = new WeakInvalidationListener(mappedListener);
    private O mapped = null;
    private Subscription mappedSubscription = null;

    public FlatMapBindingBase(ObservableValue<T> source, Function<? super T, O> mapper) {
        this.source = source;
        this.mapper = mapper;
        source.addListener(weakSourceListener);
    }

    @Override
    public final void dispose() {
        source.removeListener(weakSourceListener);
        disposeMapped();
    }

    @Override
    protected final U computeValue() {
        setupTargetObservable();
        return mapped != null ? mapped.getValue() : null;
    }

    private void setupTargetObservable() {
        if (mapped == null) {
            T baseVal = source.getValue();
            mapped = mapper.apply(baseVal);
            mappedSubscription = observeTargetObservable(mapped);
        }
    }

    protected O getTargetObservable() {
        setupTargetObservable();
        return mapped;
    }

    protected Subscription observeTargetObservable(O target) {
        target.addListener(weakMappedListener);
        return () -> target.removeListener(weakMappedListener);
    }

    private void disposeMapped() {
        if (mapped != null) {
            mappedSubscription.unsubscribe();
            mappedSubscription = null;
            mapped = null;
        }
    }

    private void mappedInvalidated() {
        invalidate();
    }

    protected void sourceInvalidated() {
        disposeMapped();
        invalidate();
    }
}

class FlatMapBinding<T, U, O extends ObservableValue<U>> extends FlatMapBindingBase<T, U, O> {

    public FlatMapBinding(ObservableValue<T> source, Function<? super T, O> mapper) {
        super(source, mapper);
    }
}

class FlatMapProperty<T, U, O extends Property<U>> extends FlatMapBindingBase<T, U, O> implements PropertyBinding<U> {
    private ObservableValue<? extends U> boundTo = null;
    private boolean resetOnUnbind = false;
    private U resetTo = null;

    public FlatMapProperty(ObservableValue<T> source, Function<? super T, O> mapper) {
        super(source, mapper);
    }

    @Override
    protected Subscription observeTargetObservable(O mapped) {
        if (boundTo != null) {
            mapped.bind(boundTo);
        }

        Subscription s1 = super.observeTargetObservable(mapped);
        Subscription s2 = () -> {
            if (boundTo != null) {
                mapped.unbind();
                if (resetOnUnbind) {
                    mapped.setValue(resetTo);
                }
            }
        };

        return s1.and(s2);
    }

    @Override
    protected void sourceInvalidated() {
        super.sourceInvalidated();

        // if bound, make sure to rebind eagerly
        if (boundTo != null) {
            getTargetObservable();
        }
    }

    @Override
    public void setValue(U value) {
        Property<U> target = getTargetObservable();
        if (target != null) {
            target.setValue(value);
        }
    }

    @Override
    public void bind(ObservableValue<? extends U> other) {
        Property<U> target = getTargetObservable();
        if (target != null) {
            target.bind(other);
        }
        boundTo = other;
        resetOnUnbind = false;
        resetTo = null;
    }

    @Override
    public void bind(ObservableValue<? extends U> other, U resetToOnUnbind) {
        Property<U> target = getTargetObservable();
        if (target != null) {
            target.bind(other);
        }
        boundTo = other;
        resetOnUnbind = true;
        resetTo = resetToOnUnbind;
    }

    @Override
    public boolean isBound() {
        return boundTo != null || (getTargetObservable() != null && getTargetObservable().isBound());
    }

    @Override
    public void unbind() {
        Property<U> target = getTargetObservable();
        if (target != null) {
            target.unbind();
        }
        boundTo = null;
    }

    @Override
    public void bindBidirectional(Property<U> other) {
        Bindings.bindBidirectional(this, other);
    }

    @Override
    public void unbindBidirectional(Property<U> other) {
        Bindings.unbindBidirectional(this, other);
    }

    @Override
    public Object getBean() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
