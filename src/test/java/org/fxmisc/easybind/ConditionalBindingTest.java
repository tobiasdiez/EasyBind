package org.fxmisc.easybind;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConditionalBindingTest {

    @Test
    public void test() {
        Property<String> target = new SimpleStringProperty();
        Property<String> source = new SimpleStringProperty("1");
        BooleanProperty condition = new SimpleBooleanProperty(true);

        Subscription sub = EasyBind.bindConditionally(target, source, condition);

        assertTrue(target.isBound());
        assertEquals("1", target.getValue());

        source.setValue("2");
        assertEquals("2", target.getValue());

        condition.set(false);
        assertFalse(target.isBound());

        source.setValue("3");
        assertEquals("2", target.getValue());

        condition.set(true);
        assertTrue(target.isBound());
        assertEquals("3", target.getValue());

        sub.unsubscribe();
        assertFalse(target.isBound());

        condition.set(false);
        condition.set(true);
        assertFalse(target.isBound());

        source.setValue("4");
        assertEquals("3", target.getValue());

        target.bind(source);
        sub.unsubscribe();
        assertTrue(target.isBound());
    }

}
