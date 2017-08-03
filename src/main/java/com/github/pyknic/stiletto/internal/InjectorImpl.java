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

import com.github.pyknic.stiletto.Inject;
import com.github.pyknic.stiletto.Injector;
import com.github.pyknic.stiletto.InjectorException;
import com.github.pyknic.stiletto.internal.graph.Node;
import com.github.pyknic.stiletto.internal.util.StringUtil;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.pyknic.stiletto.internal.InjectorBuilderUtil.findNodes;
import static com.github.pyknic.stiletto.internal.util.ReflectionUtil.traverseFields;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class InjectorImpl implements Injector {

    private final Map<Class<?>, Object> byType;
    private final Map<String, Object> byQualifier;
    private final Map<String, Set<Node<?>>> nodes;

    InjectorImpl(final Map<String, Object> byQualifier,
                 final Map<Class<?>, Object> byType,
                 final Map<String, Set<Node<?>>> nodes) {

        this.byQualifier = requireNonNull(byQualifier);
        this.byType      = requireNonNull(byType);
        this.nodes       = requireNonNull(nodes);
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

    @Override
    public Stream<Object> stream() {
        return byQualifier.values().stream();
    }

    @Override
    public <T> T create(Class<T> type) {
        return creator(type).get();
    }

    @Override
    public <T> Supplier<T> creator(Class<T> type) {
        Set<Node<?>> nodes = this.nodes.get(type.getName());
        if (nodes == null || nodes.isEmpty()) {
            nodes = findNodes(type, type.getName());
        }

        if (nodes.isEmpty()) {
            throw new InjectorException(format(
                "Could not find any way to instantiate %s.",
                type
            ));
        } else {
            return nodes.stream()
                .filter(n -> n.getDependencies().stream()
                    .allMatch(byQualifier::containsKey))
                .map(n -> (Supplier<T>) () -> {
                    @SuppressWarnings("unchecked")
                    final Node<T> tNode = (Node<T>) n;
                    return tNode.instantiate(byQualifier);
                })
                .findFirst().orElseThrow(() -> new InjectorException(format(
                    "Could not find any constructor for '%s' where all " +
                    "the parameters was injectable.",
                    type.getName()
                    ))
                );
        }
    }

    @Override
    public <T> T inject(T instance) {
        final Class<?> clazz = instance.getClass();
        traverseFields(clazz)
            .filter(f -> f.getAnnotation(Inject.class) != null)
            .forEachOrdered(f -> {
                final String qualifier = ofNullable(f.getAnnotation(Inject.class))
                    .map(Inject::value).filter(StringUtil::notEmpty)
                    .orElseGet(() -> f.getType().getName());

                if (!byQualifier.containsKey(qualifier)) {
                    throw new InjectorException(format(
                        "Field '%s' in class '%s' has the qualifier '%s' " +
                        "which is not injectable.",
                        f.getName(), clazz.getName(), qualifier
                    ));
                }

                final Object value = byQualifier.get(qualifier);
                try {
                    f.setAccessible(true);
                    if (f.getType() == long.class) {
                        f.setLong(instance, (Long) value);
                    } else if (f.getType() == int.class) {
                        f.setInt(instance, (Integer) value);
                    } else if (f.getType() == short.class) {
                        f.setShort(instance, (Short) value);
                    } else if (f.getType() == byte.class) {
                        f.setByte(instance, (Byte) value);
                    } else if (f.getType() == double.class) {
                        f.setDouble(instance, (Double) value);
                    } else if (f.getType() == float.class) {
                        f.setFloat(instance, (Float) value);
                    } else if (f.getType() == char.class) {
                        f.setChar(instance, (Character) value);
                    } else if (f.getType() == boolean.class) {
                        f.setBoolean(instance, (Boolean) value);
                    } else {
                        f.set(instance, value);
                    }
                } catch (final IllegalAccessException ex) {
                    throw new InjectorException(format(
                        "Field '%s' in class '%s' can't be accessed.",
                        f.getName(), clazz.getName()
                    ), ex);
                }
            }
        );

        return instance;
    }
}
