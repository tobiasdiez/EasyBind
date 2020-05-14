package com.tobiasdiez.easybind.select;

interface NestedSelectionElement<T, U> {
    void connect(T baseVal);

    void disconnect();

    boolean isConnected();

    U getValue();
}
