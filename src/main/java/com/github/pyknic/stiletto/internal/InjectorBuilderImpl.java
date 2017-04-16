package com.github.pyknic.stiletto.internal;

import com.github.pyknic.stiletto.Inject;
import com.github.pyknic.stiletto.Injector;
import com.github.pyknic.stiletto.InjectorBuilder;
import com.github.pyknic.stiletto.InjectorException;
import com.github.pyknic.stiletto.internal.graph.Node;
import com.github.pyknic.stiletto.internal.graph.NodeImpl;
import com.speedment.stream.MapStream;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.pyknic.stiletto.internal.util.ReflectionUtil.traverseAncestors;
import static com.github.pyknic.stiletto.internal.util.ReflectionUtil.traverseFields;
import static java.util.Collections.unmodifiableSet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class InjectorBuilderImpl implements InjectorBuilder {

    public static InjectorBuilder builder() {
        return new InjectorBuilderImpl();
    }

    private static final Predicate<String> NOT_EMPTY = s -> !s.isEmpty();
    private static final Predicate<Constructor<?>> INJECTABLE =
        c -> c.getAnnotation(Inject.class) != null;

    private final Map<String, Set<Node<?>>> injectables;

    private InjectorBuilderImpl() {
        this.injectables = new LinkedHashMap<>();
    }

    @Override
    public <T> InjectorBuilder withType(Class<T> clazz, String qualifier) {

        // Determine if there are any members that need to be injected.
        final Set<String> dependencies = unmodifiableSet(traverseFields(clazz)
            .map(f -> ofNullable(f.getAnnotation(Inject.class))
                .map(Inject::value).filter(NOT_EMPTY)
                .orElseGet(() -> f.getType().getName())
            ).collect(toSet())
        );


        // If the specified type has at least one annotated constructor, we
        // should only look at them. Otherwise, consider all constructors
        // candidates for injection.
        Stream<Constructor<?>> constructors = Stream.of(clazz.getDeclaredConstructors());
        if (Stream.of(clazz.getConstructors()).anyMatch(INJECTABLE)) {
            constructors = constructors.filter(INJECTABLE);
        }

        // Compute the necessary dependencies for every constructor and add
        // them to the map.
        final Set<Node<?>> byQualifier = new LinkedHashSet<>();
        constructors.forEach(constr -> {
            final Set<String> deps = new HashSet<>(dependencies);

            Stream.of(constr.getParameters())
                .map(p -> ofNullable(p.getAnnotation(Inject.class))
                    .map(Inject::value).filter(NOT_EMPTY)
                    .orElseGet(() -> p.getType().getName())
                ).forEach(deps::add);

            byQualifier.add(new NodeImpl<>(
                qualifier,
                unmodifiableSet(deps),
                constr
            ));
        });

        injectables.put(qualifier, unmodifiableSet(byQualifier));
        return this;
    }

    @Override
    public Injector build() {
        final Map<String, Object> byQualifier = new HashMap<>();
        final Map<Class<?>, Object> byType    = new HashMap<>();

        final Predicate<Node<?>> canBeInstantiated =
            i -> i.getDependencies().stream()
                .allMatch(byQualifier.keySet()::contains);

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
                            .map(i -> "  " + i.getQualifier() + " -> [" +
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

        return new InjectorImpl(byQualifier, byType);
    }
}