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

import com.github.pyknic.stiletto.internal.InjectorBuilderImpl;

import java.util.Optional;

import static com.github.pyknic.stiletto.InjectorException.unknownQualifierException;
import static com.github.pyknic.stiletto.InjectorException.unknownTypeException;

/**
 * An immutable injector that can be queried for instances of a particular
 * {@link #get(Class) type} or {@link #get(String) qualifier}. To obtain an
 * instance of this interface, use the static {@link #builder()} constructor.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public interface Injector {

    /**
     * Creates a new builder for this type of Injector using the default
     * implementation. Once the instance is built, this instance will be
     * immutable.
     *
     * @return  the created builder
     */
    static InjectorBuilder builder() {
        return InjectorBuilderImpl.builder();
    }

    /**
     * Returns {@code true} if this injector contains an instance of the
     * specified type or supertype.
     *
     * @param <T>   the type
     * @param type  the type
     * @return      {@code true} if instance is found, else {@code false}
     */
    default <T> boolean has(Class<T> type) {
        return get(type).isPresent();
    }

    /**
     * Returns the instance for the specified type or supertype. If no such
     * exists, then an {@link InjectorException} is thrown.
     *
     * @param <T>   the type
     * @param type  the type
     * @return      the instance
     */
    default <T> T getOrThrow(Class<T> type) throws InjectorException {
        return get(type).orElseThrow(() -> unknownTypeException(type));
    }

    /**
     * Returns the instance for the specified type or supertype. If no such
     * exists, then an empty {@code Optional} is returned.
     *
     * @param <T>   the type
     * @param type  the type
     * @return      the instance, or empty
     */
    <T> Optional<T> get(Class<T> type);

    /**
     * Returns {@code true} if this injector contains an instance of the
     * specified qualifier.
     *
     * @param qualifier  the qualifier (usually the class name)
     * @return           {@code true} if instance is found, else {@code false}
     */
    default boolean has(String qualifier) {
        return get(qualifier).isPresent();
    }

    /**
     * Returns the instance for the specified qualifier. If no such
     * exists, then an {@link InjectorException} is thrown.
     *
     * @param <T>        the type
     * @param qualifier  the qualifier
     * @return           the instance
     */
    @SuppressWarnings("unchecked")
    default <T> T getOrThrow(String qualifier) {
        return (T) get(qualifier).orElseThrow(
            () -> unknownQualifierException(qualifier)
        );
    }

    /**
     * Returns the instance for the specified qualifier. If no such
     * exists, then an empty {@code Optional} is returned.
     *
     * @param <T>        the type
     * @param qualifier  the type
     * @return           the instance, or empty
     */
    <T> Optional<T> get(String qualifier);

}
