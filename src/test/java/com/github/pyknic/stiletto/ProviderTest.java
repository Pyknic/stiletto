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

import com.github.pyknic.stiletto.testprovider.NotProvided;
import com.github.pyknic.stiletto.testprovider.Providable;
import com.github.pyknic.stiletto.testprovider.Provided;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Emil Forslund
 * @since  1.0.4
 */
public final class ProviderTest {

    @Test
    void testProvided() {
        Injector injector = Injector.builder()
            .fromProviders(Providable.class.getPackage().getName())
            .build();

        final Optional<Providable> providable = injector.get(Providable.class);
        assertTrue(providable.isPresent());
        assertEquals(Provided.class, providable.get().getClass());
        assertTrue(injector.stream()
            .map(Object::getClass)
            .noneMatch(NotProvided.class::equals)
        );
    }

    @Test
    void testWithNotProvidedBefore() {
        Injector injector = Injector.builder()
            .withType(NotProvided.class)
            .fromProviders(Providable.class.getPackage().getName())
            .build();

        final Optional<Providable> providable = injector.get(Providable.class);
        assertTrue(providable.isPresent());
        assertEquals(Provided.class, providable.get().getClass());
        assertFalse(injector.stream()
            .map(Object::getClass)
            .noneMatch(NotProvided.class::equals)
        );
    }

    @Test
    void testWithNotProvidedAfter() {
        Injector injector = Injector.builder()
            .fromProviders(Providable.class.getPackage().getName())
            .withType(NotProvided.class)
            .build();

        final Optional<Providable> providable = injector.get(Providable.class);
        assertTrue(providable.isPresent());
        assertEquals(NotProvided.class, providable.get().getClass());
        assertFalse(injector.stream()
            .map(Object::getClass)
            .noneMatch(NotProvided.class::equals)
        );
    }
}
