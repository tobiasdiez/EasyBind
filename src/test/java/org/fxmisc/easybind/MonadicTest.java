package org.fxmisc.easybind;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.fxmisc.easybind.monadic.MonadicBinding;
import org.fxmisc.easybind.monadic.PropertyBinding;
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
    public void flatMapTest() {
        Property<A> base = new SimpleObjectProperty<>();
        MonadicBinding<String> flat = EasyBind.monadic(base).flatMap(a -> a.b).flatMap(b -> b.s);

        Counter invalidationCounter = new Counter();
        flat.addListener(obs -> invalidationCounter.inc());

        assertNull(flat.getValue());

        A a = new A();
        B b = new B();
        b.s.setValue("s1");
        a.b.setValue(b);
        base.setValue(a);
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("s1", flat.getValue());

        a.b.setValue(new B());
        assertEquals(1, invalidationCounter.getAndReset());
        assertNull(flat.getValue());

        b.s.setValue("s2");
        assertEquals(0, invalidationCounter.getAndReset());
        assertNull(flat.getValue());

        a.b.getValue().s.setValue("x");
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("x", flat.getValue());

        a.b.setValue(null);
        assertEquals(1, invalidationCounter.getAndReset());
        assertNull(flat.getValue());

        a.b.setValue(b);
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("s2", flat.getValue());
    }

    @Test
    public void selectPropertyTest() {
        Property<A> base = new SimpleObjectProperty<>();
        PropertyBinding<String> selected = EasyBind.monadic(base).flatMap(a -> a.b).selectProperty(b -> b.s);

        Counter invalidationCounter = new Counter();
        selected.addListener(obs -> invalidationCounter.inc());

        assertNull(selected.get());

        selected.setValue("will be discarded");
        assertNull(selected.get());
        assertEquals(0, invalidationCounter.getAndReset());

        Property<String> src = new SimpleStringProperty();

        selected.bind(src);
        assertNull(selected.get());
        assertEquals(0, invalidationCounter.getAndReset());

        src.setValue("1");
        assertNull(selected.get());
        assertEquals(0, invalidationCounter.getAndReset());

        A a = new A();
        B b = new B();
        b.s.setValue("X");
        a.b.setValue(b);
        base.setValue(a);

        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("1", selected.get());
        assertEquals("1", b.s.getValue());

        src.setValue("2");
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("2", selected.get());
        assertEquals("2", b.s.getValue());

        B b2 = new B();
        b2.s.setValue("Y");
        a.b.setValue(b2);
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("2", b2.s.getValue());
        assertEquals("2", selected.get());

        src.setValue("3");
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("3", b2.s.getValue());
        assertEquals("3", selected.get());
        assertEquals("2", b.s.getValue());

        base.setValue(null);
        assertEquals(1, invalidationCounter.getAndReset());
        assertNull(selected.get());
        assertFalse(b2.s.isBound());

        base.setValue(a);
        assertEquals(1, invalidationCounter.getAndReset());
        assertEquals("3", selected.get());
        assertTrue(b2.s.isBound());

        selected.unbind();
        assertEquals(0, invalidationCounter.getAndReset());
        src.setValue("4");
        assertEquals("3", b2.s.getValue());
        assertEquals("3", selected.get());
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
        PropertyBinding<String> selected = EasyBind.monadic(base).flatMap(a -> a.b).selectProperty(b -> b.s);
        StringProperty source = new SimpleStringProperty("A");

        selected.bind(source, "X");

        assertEquals(null, selected.get());

        A a = new A();
        B b = new B();
        a.b.setValue(b);
        base.setValue(a);
        assertEquals("A", selected.get());
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

        MonadicBinding<String> firstNonNull = EasyBind.monadic(s1).orElse(s2).orElse(s3);
        assertEquals("a", firstNonNull.getValue());

        s2.set(null);
        assertEquals("a", firstNonNull.getValue());

        s1.set(null);
        assertEquals("c", firstNonNull.getValue());

        s2.set("b");
        assertEquals("b", firstNonNull.getValue());

        s2.set(null);
        s3.set(null);
        assertNull(firstNonNull.getValue());
    }

}
