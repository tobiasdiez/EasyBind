package com.tobiasdiez.easybind;

import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListBindText {

    @Test
    public void test() {
        ObservableList<String> source = FXCollections.observableArrayList();
        source.addAll("a", "b", "c");

        ObservableList<String> target = FXCollections.observableArrayList();
        Subscription sub = EasyBind.listBind(target, source);

        assertEquals(source, target);

        source.addAll(2, Arrays.asList("b", "a"));
        assertEquals(Arrays.asList("a", "b", "b", "a", "c"), target);

        source.remove(1, 3);
        assertEquals(Arrays.asList("a", "a", "c"), target);

        sub.unsubscribe();
        source.add("d");
        assertEquals(Arrays.asList("a", "a", "c"), target);
    }

}
