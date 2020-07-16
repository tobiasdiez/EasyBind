package com.tobiasdiez.easybind;

import java.lang.ref.WeakReference;
import java.util.List;
import javafx.beans.WeakListener;
import javafx.collections.ListChangeListener;

/**
 * Change listener that propagates changes to the given list.
 *
 * @implNote Copy of {@link com.sun.javafx.binding.ContentBinding.ListContentBinding}.
 */
class ListContentBinding<E> implements ListChangeListener<E>, WeakListener {
    private final WeakReference<List<E>> listRef;

    public ListContentBinding(List<E> list) {
        // We use a weak reference, since we want to allow the target list to be garbage collected
        // even though this listener is still registered
        // Reason: There is no point in updating a list that is no longer used
        this.listRef = new WeakReference<>(list);
    }

    @Override
    public void onChanged(ListChangeListener.Change<? extends E> change) {
        final List<E> list = listRef.get();
        if (list == null) {
            change.getList().removeListener(this);
        } else {
            while (change.next()) {
                if (change.wasPermutated()) {
                    list.subList(change.getFrom(), change.getTo()).clear();
                    list.addAll(change.getFrom(), change.getList().subList(change.getFrom(), change.getTo()));
                } else {
                    if (change.wasRemoved()) {
                        list.subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
                    }
                    if (change.wasAdded()) {
                        list.addAll(change.getFrom(), change.getAddedSubList());
                    }
                }
            }
        }
    }

    @Override
    public boolean wasGarbageCollected() {
        return listRef.get() == null;
    }

    @Override
    public int hashCode() {
        final List<E> list = listRef.get();
        return (list == null) ? 0 : list.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        final List<E> list1 = listRef.get();
        if (list1 == null) {
            return false;
        }

        if (obj instanceof ListContentBinding) {
            final ListContentBinding<?> other = (ListContentBinding<?>) obj;
            final List<?> list2 = other.listRef.get();
            return list1 == list2;
        }
        return false;
    }
}

