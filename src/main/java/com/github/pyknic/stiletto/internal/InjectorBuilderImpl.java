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
import com.github.pyknic.stiletto.InjectorBuilder;
import com.github.pyknic.stiletto.InjectorException;
import com.github.pyknic.stiletto.Provider;
import com.github.pyknic.stiletto.internal.graph.Node;
import com.speedment.stream.MapStream;

import java.util.*;
import java.util.function.Predicate;

import static com.github.pyknic.stiletto.internal.InjectorBuilderUtil.findNodes;
import static com.github.pyknic.stiletto.internal.util.ReflectionUtil.traverseAncestors;
import static com.github.pyknic.stiletto.internal.util.ReflectionUtil.traverseFields;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import static java.util.Collections.unmodifiableSet;
import static java.util.Optional.ofNullable;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.joining;

/**
 * Default implementation of the {@link InjectorBuilder}-interface.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class InjectorBuilderImpl implements InjectorBuilder {

    public static InjectorBuilder builder() {
        return new InjectorBuilderImpl();
    }

    private final Map<String, Set<Node<?>>> injectables;

    private InjectorBuilderImpl() {
        this.injectables = new LinkedHashMap<>();
    }

    @Override
    public <T> InjectorBuilder withType(Class<T> clazz, String qualifier) {
        injectables.put(qualifier, findNodes(clazz, qualifier));
        return this;
    }

    @Override
    public <T> InjectorBuilder fromProviders() {
        InjectorBuilder b = this;
        new FastClasspathScanner().matchClassesWithAnnotation(Provider.class, c -> {
            Provider p = c.getAnnotation(Provider.class);
            if(p.value().isEmpty())
                b.withType(c);
            else
                b.withType(c, p.value());
        });
        return this;
    }
    
    @Override
    public Injector build() {
        final Map<String, Object> byQualifier = new HashMap<>();
        final Map<Class<?>, Object> byType    = new HashMap<>();

        final Predicate<Node<?>> canBeInstantiated =
            i -> i.getDependencies().stream()
                .allMatch(byQualifier.keySet()::contains);

        final Map<String, Set<Node<?>>> nodes =
            unmodifiableMap(new LinkedHashMap<>(injectables));

        while (!injectables.isEmpty()) {
            final Set<String> resolved = new HashSet<>();

            MapStream.of(injectables)
                .flatMapValue(v -> v.stream()
                    .filter(canBeInstantiated)
                    .limit(1)
                ).forEachOrdered(i -> {
                final Object inst = i.getValue().instantiate(byQualifier);

                byQualifier.put(i.getKey(), inst);
                resolved.add(i.getKey());

                traverseAncestors(inst.getClass())
                    .forEach(c -> {
                        byType.put(c, inst);
                        byQualifier.put(c.getName(), inst);
                    });
            });

            if (resolved.size() == 0) {
                throw new InjectorException(
                    "Error! Can't resolve dependencies for the " +
                    "following qualifiers: " + injectables.keySet() +
                    ".\nThe following dependencies have not been resolved: [\n" +
                        injectables.values().stream()
                            .flatMap(Set::stream)
                            .map(i -> "  " + i.getQualifier() + " -> [\n    " +
                                i.getDependencies().stream()
                                 .filter(d -> !byQualifier.keySet().contains(d))
                                 .collect(joining(",\n    "))
                            )
                            .collect(joining(",\n")) +
                    "]."
                );
            } else {
                resolved.forEach(injectables::remove);
            }
        }

        return new InjectorImpl(byQualifier, byType, nodes);
    }
}
