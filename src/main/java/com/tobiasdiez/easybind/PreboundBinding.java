package com.tobiasdiez.easybind;

import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;

/**
 * Object binding that binds to its dependencies on creation
 * and unbinds from them on dispose. If one of the registered dependencies becomes invalid, this
 * binding is marked as invalid.
 * To provide a concrete implementation of this class, the method {@link #computeValue()}
 * has to be implemented to calculate the value of this binding based on the current state of the dependencies.
 * This method is called when {@link #get()} is invoked for an invalid binding.
 */
public abstract class PreboundBinding<T> extends ObjectBinding<T> {
    protected final Observable[] dependencies;

    public PreboundBinding(Observable... dependencies) {
        this.dependencies = dependencies;
        bind(dependencies);
    }

    @Override
    public void dispose() {
        unbind(dependencies);
    }
}
