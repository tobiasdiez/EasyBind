package com.tobiasdiez.easybind;

import java.util.Optional;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.tobiasdiez.easybind.optional.OptionalBinding;
import com.tobiasdiez.easybind.optional.PropertyBinding;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MonadicTest {

    private static class A {
        public final Property<B> b = new SimpleObjectProperty<>();
    }

    private static class B {
        public final Property<String> s = new SimpleStringProperty();
    }

    @Test
    public void mapObsTest() {
        Property<A> base = new SimpleObjectProperty<>();
        OptionalBinding<String> flat = EasyBind.wrapNullable(base).mapObservable(a -> a.b).mapObservable(b -> b.s);

        Counter invalidationCounter = new Counter();
        flat.addListener(obs -> invalidationCounter.inc());

        assertEquals(Optional.empty(), flat.getValue());

        A a = new A();
        B b = new B();
        b.s.setValue("s1");
        a.b.setValue(b);
        base.setValue(a);
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals(Optional.of("s1"), flat.getValue());

        a.b.setValue(new B());
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals(Optional.empty(), flat.getValue());

        b.s.setValue("s2");
        assertEquals(0, invalidationCounter.getAndReset());
        assertEquals(Optional.empty(), flat.getValue());

        a.b.getValue().s.setValue("x");
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals(Optional.of("x"), flat.getValue());

        a.b.setValue(null);
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals(Optional.empty(), flat.getValue());

        a.b.setValue(b);
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals(Optional.of("s2"), flat.getValue());
    }

    @Test
    public void selectPropertyTest() {
        Property<A> base = new SimpleObjectProperty<>();
        PropertyBinding<String> selected = EasyBind.wrapNullable(base).mapObservable(a -> a.b).selectProperty(b -> b.s);

        Counter invalidationCounter = new Counter();
        selected.addListener(obs -> invalidationCounter.inc());

        assertNull(selected.getValue());

        selected.setValue("will be discarded");
        assertNull(selected.getValue());
        assertEquals(0, invalidationCounter.getAndReset());

        Property<String> src = new SimpleStringProperty();

        selected.bind(src);
        assertNull(selected.getValue());
        assertEquals(0, invalidationCounter.getAndReset());

        src.setValue("1");
        assertNull(selected.getValue());
        assertEquals(0, invalidationCounter.getAndReset());

        A a = new A();
        B b = new B();
        b.s.setValue("X");
        a.b.setValue(b);
        base.setValue(a);

        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("1", selected.getValue());
        assertEquals("1", b.s.getValue());

        src.setValue("2");
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("2", selected.getValue());
        assertEquals("2", b.s.getValue());

        B b2 = new B();
        b2.s.setValue("Y");
        a.b.setValue(b2);
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("2", b2.s.getValue());
        assertEquals("2", selected.getValue());

        src.setValue("3");
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("3", b2.s.getValue());
        assertEquals("3", selected.getValue());
        assertEquals("2", b.s.getValue());

        base.setValue(null);
        assertEquals(1, invalidationCounter.getAndReset());
        assertNull(selected.getValue());
        assertFalse(b2.s.isBound());

        base.setValue(a);
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("3", selected.getValue());
        assertTrue(b2.s.isBound());

        selected.unbind();
        assertEquals(0, invalidationCounter.getAndReset());
        src.setValue("4");
        assertEquals("3", b2.s.getValue());
        assertEquals("3", selected.getValue());
        assertEquals("2", b.s.getValue());

        a.b.setValue(b);
        selected.setValue("5");
        assertEquals("5", b.s.getValue());

        a.b.setValue(null);
        selected.bind(src);
        a.b.setValue(b2);
        assertTrue(b2.s.isBound());
    }

    @Test
    public void selectPropertyResetTest() {
        Property<A> base = new SimpleObjectProperty<>();
        PropertyBinding<String> selected = EasyBind.wrapNullable(base).mapObservable(a -> a.b).selectProperty(b -> b.s);
        StringProperty source = new SimpleStringProperty("A");

        selected.bind(source, "X");

        assertNull(selected.getValue());

        A a = new A();
        B b = new B();
        a.b.setValue(b);
        base.setValue(a);
        assertEquals("A", selected.getValue());
        assertEquals("A", b.s.getValue());

        B b2 = new B();
        a.b.setValue(b2);
        assertEquals("A", b2.s.getValue());
        assertEquals("X", b.s.getValue());

        base.setValue(null);
        assertEquals("X", b2.s.getValue());
    }

    @Test
    public void orElseTest() {
        StringProperty s1 = new SimpleStringProperty("a");
        StringProperty s2 = new SimpleStringProperty("b");
        StringProperty s3 = new SimpleStringProperty("c");

        OptionalBinding<String> firstNonNull = EasyBind.wrapNullable(s1).orElse(s2).orElse(s3);
        assertEquals(Optional.of("a"), firstNonNull.getValue());

        s2.set(null);
        assertEquals(Optional.of("a"), firstNonNull.getValue());

        s1.set(null);
        assertEquals(Optional.of("c"), firstNonNull.getValue());

        s2.set("b");
        assertEquals(Optional.of("b"), firstNonNull.getValue());

        s2.set(null);
        s3.set(null);
        assertEquals(Optional.empty(), firstNonNull.getValue());
    }

}
