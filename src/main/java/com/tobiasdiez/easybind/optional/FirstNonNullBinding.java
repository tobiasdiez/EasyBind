package com.tobiasdiez.easybind.optional;

import java.util.Optional;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ObservableValue;

class FirstNonNullBinding<T> extends PreboundOptionalBinding<T> {
    private final ObservableValue<? extends T>[] chain;
    private final InvalidationListener listener = this::srcInvalidated;
    private final InvalidationListener weakListener = new WeakInvalidationListener(listener);
    private int startAt = 0;

    @SafeVarargs
    public FirstNonNullBinding(ObservableValue<? extends T>... chain) {
        super(chain);

        this.chain = chain;
        for (ObservableValue<? extends T> observableValue : chain) {
            observableValue.addListener(weakListener);
        }
    }

    @Override
    public void dispose() {
        for (ObservableValue<? extends T> observableValue : chain) {
            observableValue.removeListener(weakListener);
        }
    }

    @Override
    protected Optional<T> computeValue() {
        for (int i = startAt; i < chain.length; ++i) {
            T val = chain[i].getValue();
            if (val != null) {
                startAt = i;
                return Optional.of(val);
            }
        }
        startAt = chain.length;
        return Optional.empty();
    }

    private void srcInvalidated(Observable src) {
        for (int i = 0; i < chain.length; ++i) {
            if (chain[i] == src) {
                if (i <= startAt) {
                    startAt = i;
                    invalidate();
                }
                break;
            }
        }
    }
}
