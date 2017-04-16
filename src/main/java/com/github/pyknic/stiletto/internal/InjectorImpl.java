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
package com.github.pyknic.stiletto.internal;

import com.github.pyknic.stiletto.Injector;

import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class InjectorImpl implements Injector {

    private final Map<Class<?>, Object> byType;
    private final Map<String, Object> byQualifier;

    InjectorImpl(Map<String, Object> byQualifier, Map<Class<?>, Object> byType) {
        this.byQualifier = requireNonNull(byQualifier);
        this.byType      = requireNonNull(byType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(Class<T> type) {
        return Optional.ofNullable((T) byType.get(type));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String qualifier) {
        return Optional.ofNullable((T) byQualifier.get(qualifier));
    }
}
