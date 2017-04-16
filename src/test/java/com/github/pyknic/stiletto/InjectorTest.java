package com.github.pyknic.stiletto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
@DisplayName("Injector")
class InjectorTest {

    private interface CompA {}
    private interface CompB {}
    private interface CompC {}

    private static final class CompAImpl implements CompA {}
    private static final class CompAImpl2 implements CompA {}
    private static final class CompBImpl implements CompA, CompB {
        private final CompA wrapped;
        private final CompA wrapped2;

        // Should not be invoked.
        CompBImpl() {
            wrapped = null;
            wrapped2 = null;
        }

        @Inject
        CompBImpl(@Inject("a") CompA wrapped, CompA second) {
            this.wrapped  = wrapped;
            this.wrapped2 = second;
        }
    }

    @Test
    @DisplayName(".has(Class)")
    void has() {
        final Injector inj = Injector.builder()
            .withType(CompAImpl.class, "a")
            .withType(CompBImpl.class)
            .build();

        assertTrue(inj.has(CompA.class), "CompA interface");
        assertTrue(inj.has(CompB.class), "CompB interface");
        assertFalse(inj.has(CompC.class), "CompC interface");

        assertTrue(inj.has(CompAImpl.class), "CompA class");
        assertTrue(inj.has(CompBImpl.class), "CompB class");
    }

    @Test
    @DisplayName(".getOrThrow(Class)")
    void getOrThrow() {
        final Injector inj = Injector.builder()
            .withType(CompAImpl.class, "a")
            .withType(CompBImpl.class)
            .build();

        assertNotNull(inj.getOrThrow(CompA.class), "CompA interface");
        assertNotNull(inj.getOrThrow(CompB.class), "CompB interface");
        assertNotNull(inj.getOrThrow(CompAImpl.class), "CompA class");
        assertNotNull(inj.getOrThrow(CompBImpl.class), "CompB class");
        assertThrows(InjectorException.class, () -> {
            inj.getOrThrow(CompC.class);
        });

        assertEquals(CompBImpl.class, inj.getOrThrow(CompA.class).getClass(), "CompA is a CompBImpl (most recent added)");
        assertEquals(CompBImpl.class, inj.getOrThrow(CompB.class).getClass(), "CompB is a CompBImpl");

        assertNotNull(inj.getOrThrow(CompBImpl.class).wrapped, "Test wrapped instance");
        assertEquals(CompAImpl.class, inj.getOrThrow(CompBImpl.class).wrapped.getClass(), "Test wrapped instance type");
    }

    @Test
    @DisplayName(".get(Class)")
    void get() {
        final Injector inj = Injector.builder()
            .withType(CompAImpl.class, "a")
            .withType(CompBImpl.class)
            .build();

        assertTrue(inj.get(CompA.class).isPresent(), "CompA interface");
        assertTrue(inj.get(CompB.class).isPresent(), "CompB interface");
        assertFalse(inj.get(CompC.class).isPresent(), "CompC interface");

        assertTrue(inj.get(CompAImpl.class).isPresent(), "CompA class");
        assertTrue(inj.get(CompBImpl.class).isPresent(), "CompB class");
    }

    @Test
    @DisplayName(".has(String)")
    void hasQualifier() {
        final Injector inj = Injector.builder()
            .withType(CompAImpl.class, "a")
            .withType(CompAImpl2.class, "a2")
            .withType(CompBImpl.class, "b")
            .build();

        assertTrue(inj.has("a"), "'a' qualifier");
        assertTrue(inj.has("a2"), "'a2' qualifier");
        assertTrue(inj.has("b"), "'b' qualifier");
        assertFalse(inj.has("c"), "'c' qualifier");
    }

    @Test
    @DisplayName(".getOrThrow(String)")
    void getOrThrowQualifier() {
        final Injector inj = Injector.builder()
            .withType(CompAImpl.class, "a")
            .withType(CompAImpl2.class, "a2")
            .withType(CompBImpl.class, "b")
            .build();

        assertNotNull(inj.getOrThrow(CompA.class), "CompA interface");
        assertNotNull(inj.getOrThrow(CompB.class), "CompB interface");
        assertNotNull(inj.getOrThrow(CompAImpl.class), "CompA class");
        assertNotNull(inj.getOrThrow(CompBImpl.class), "CompB class");
        assertThrows(InjectorException.class, () -> {
            inj.getOrThrow(CompC.class);
        });

        assertNotNull(inj.getOrThrow("a"), "'a' qualifier");
        assertNotNull(inj.getOrThrow("a2"), "'a2' qualifier");
        assertNotNull(inj.getOrThrow("b"), "'b' qualifier");
        assertThrows(InjectorException.class, () -> {
            inj.getOrThrow("c");
        });

        assertEquals(CompBImpl.class, inj.getOrThrow(CompA.class).getClass(), "CompA is a CompBImpl (most recent added)");
        assertEquals(CompBImpl.class, inj.getOrThrow(CompB.class).getClass(), "CompB is a CompBImpl");

        assertEquals(CompAImpl.class, inj.getOrThrow("a").getClass(), "'a' is a CompAImpl");
        assertEquals(CompAImpl2.class, inj.getOrThrow("a2").getClass(), "'a2' is a CompAImpl2");
        assertEquals(CompBImpl.class, inj.getOrThrow("b").getClass(), "'b' is a CompBImpl");

        assertNotNull(inj.getOrThrow(CompBImpl.class).wrapped, "Test wrapped instance");
        assertNotNull(inj.getOrThrow(CompBImpl.class).wrapped2, "Test wrapped2 instance");
        assertEquals(CompAImpl.class, inj.getOrThrow(CompBImpl.class).wrapped.getClass(), "Test wrapped instance type");
        assertEquals(CompAImpl2.class, inj.getOrThrow(CompBImpl.class).wrapped2.getClass(), "Test wrapped2 instance type (most recent added)");

        assertNotNull(((CompBImpl) inj.getOrThrow("b")).wrapped, "Test wrapped instance");
        assertNotNull(((CompBImpl) inj.getOrThrow("b")).wrapped2, "Test wrapped2 instance");
        assertEquals(CompAImpl.class, ((CompBImpl) inj.getOrThrow("b")).wrapped.getClass(), "Test wrapped instance type");
        assertEquals(CompAImpl2.class, ((CompBImpl) inj.getOrThrow("b")).wrapped2.getClass(), "Test wrapped2 instance type (most recent added)");
    }

    @Test
    @DisplayName(".get(String)")
    void getQualifier() {
        final Injector inj = Injector.builder()
            .withType(CompAImpl.class, "a")
            .withType(CompBImpl.class, "b")
            .build();

        assertTrue(inj.get("a").isPresent(), "'a' qualifier");
        assertTrue(inj.get("b").isPresent(), "'b' qualifier");
        assertFalse(inj.get("c").isPresent(), "'c' qualifier");
    }
}