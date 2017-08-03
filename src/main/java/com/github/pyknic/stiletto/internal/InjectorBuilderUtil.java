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
import com.github.pyknic.stiletto.internal.graph.Node;
import com.github.pyknic.stiletto.internal.graph.NodeImpl;
import com.github.pyknic.stiletto.internal.util.StringUtil;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.pyknic.stiletto.internal.util.ReflectionUtil.traverseFields;
import static java.util.Collections.unmodifiableSet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

/**
 * Utility class to reduce code in {@link InjectorBuilderImpl} and
 * {@link InjectorImpl}.
 *
 * @author Emil Forslund
 * @since  1.0.3
 */
final class InjectorBuilderUtil {

    /**
     * Returns an immutable set of nodes that can be used to instantiate the
     * specified class. This method uses reflection to scan over all the classes
     * fields and constructors to determine what dependencies every constructor
     * comes with. If at least one constructor has the
     * {@link Inject}-annotation, then only constructors with that annotation
     * will be considered.
     *
     * @param <T>        the type
     * @param clazz      the class to get nodes for
     * @param qualifier  the qualifier
     *
     * @return           immutable set of nodes for the given class
     */
    static <T> Set<Node<?>> findNodes(Class<T> clazz, String qualifier) {

        // Determine if there are any members that need to be injected.
        final Set<String> dependencies = unmodifiableSet(traverseFields(clazz)
            .filter(f -> f.getAnnotation(Inject.class) != null)
            .map(f -> ofNullable(f.getAnnotation(Inject.class))
                .map(Inject::value).filter(StringUtil::notEmpty)
                .orElseGet(() -> f.getType().getName())
            ).collect(toSet())
        );

        // If the specified type has at least one annotated constructor, we
        // should only look at them. Otherwise, consider all constructors
        // candidates for injection.
        Stream<Constructor<?>> constructors = Stream.of(clazz.getDeclaredConstructors());
        if (Stream.of(clazz.getDeclaredConstructors()).anyMatch(INJECTABLE)) {
            constructors = constructors.filter(INJECTABLE);
        }

        // Compute the necessary dependencies for every constructor and add
        // them to the map.
        final Set<Node<?>> nodes = new LinkedHashSet<>();
        constructors.forEach(constr -> {
            final Set<String> deps = new HashSet<>(dependencies);

            Stream.of(constr.getParameters())
                .map(p -> ofNullable(p.getAnnotation(Inject.class))
                    .map(Inject::value).filter(StringUtil::notEmpty)
                    .orElseGet(() -> p.getType().getName())
                ).forEach(deps::add);

            nodes.add(new NodeImpl<>(
                qualifier,
                unmodifiableSet(deps),
                constr
            ));
        });

        return unmodifiableSet(nodes);
    }

    private InjectorBuilderUtil() {}

    /**
     * Common predicate for testing if a constructor has the
     * {@link Inject}-annotation.
     */
    private static final Predicate<Constructor<?>> INJECTABLE =
        c -> c.getAnnotation(Inject.class) != null;

}
