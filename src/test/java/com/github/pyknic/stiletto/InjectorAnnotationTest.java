/**
 *
 * Copyright (c) 2017, Emil Forslund. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.pyknic.stiletto;

import com.github.pyknic.stiletto.testtype.CompA;
import com.github.pyknic.stiletto.testtype.CompAImpl;
import com.github.pyknic.stiletto.testtype.CompAImpl2;
import com.github.pyknic.stiletto.testtype.CompB;
import com.github.pyknic.stiletto.testtype.CompBImpl;
import com.github.pyknic.stiletto.testtype.CompC;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Emil Forslund & Simon Jonasson
 * @since  1.0.3
 */
@DisplayName("InjectorAnnotation")
public class InjectorAnnotationTest {

    @Test
    @DisplayName(".has(Class)")
    public void has() {
        final Injector inj = Injector.builder()
            .fromProviders()
            .build();

        assertTrue(inj.has(CompA.class), "CompA interface");
        assertTrue(inj.has(CompB.class), "CompB interface");
        assertFalse(inj.has(CompC.class), "CompC interface");

        assertTrue(inj.has(CompAImpl.class), "CompA class");
        assertTrue(inj.has(CompBImpl.class), "CompB class");
    }

    @Test
    @DisplayName(".getOrThrow(Class)")
    public void getOrThrow() {
        final Injector inj = Injector.builder()
            .fromProviders()
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
    public void get() {
        final Injector inj = Injector.builder()
            .fromProviders()
            .build();

        assertTrue(inj.get(CompA.class).isPresent(), "CompA interface");
        assertTrue(inj.get(CompB.class).isPresent(), "CompB interface");
        assertFalse(inj.get(CompC.class).isPresent(), "CompC interface");

        assertTrue(inj.get(CompAImpl.class).isPresent(), "CompA class");
        assertTrue(inj.get(CompBImpl.class).isPresent(), "CompB class");
    }

    @Test
    @DisplayName(".has(String)")
    public void hasQualifier() {
        final Injector inj = Injector.builder()
            .fromProviders()
            .build();

        assertTrue(inj.has("a"), "'a' qualifier");
        assertTrue(inj.has("a2"), "'a2' qualifier");
        assertTrue(inj.has("b"), "'b' qualifier");
        assertFalse(inj.has("c"), "'c' qualifier");
    }

    @Test
    @DisplayName(".getOrThrow(String)")
    public void getOrThrowQualifier() {
        final Injector inj = Injector.builder()
            .fromProviders()
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
    public void getQualifier() {
        final Injector inj = Injector.builder()
            .fromProviders()
            .build();

        assertTrue(inj.get("a").isPresent(), "'a' qualifier");
        assertTrue(inj.get("b").isPresent(), "'b' qualifier");
        assertFalse(inj.get("c").isPresent(), "'c' qualifier");
    }
}
