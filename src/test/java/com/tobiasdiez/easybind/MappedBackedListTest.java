package com.tobiasdiez.easybind;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MappedBackedListTest {

    @Test
    public void testSortedListUpdatesWithMappedBackedList() {
        ObservableList<IntegerProperty> list = FXCollections.observableArrayList(number -> new Observable[]{number});
        ObservableList<Integer> mappedList = EasyBind.mapBacked(list, IntegerProperty::get);
        SortedList<Integer> sortedList = new SortedList<>(mappedList);

        IntegerProperty number = new SimpleIntegerProperty(1);
        list.add(number);

        assertEquals(1, sortedList.get(0));

        number.set(2);

        assertEquals(2, sortedList.get(0));
    }
}
