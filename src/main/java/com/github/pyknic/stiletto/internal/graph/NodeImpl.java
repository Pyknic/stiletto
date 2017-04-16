package com.github.pyknic.stiletto.internal.graph;

import com.github.pyknic.stiletto.Inject;
import com.github.pyknic.stiletto.InjectorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.github.pyknic.stiletto.internal.util.ReflectionUtil.traverseFields;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

/**
 * Default implementation of the {@link Node} interface.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class NodeImpl<T> implements Node<T> {

    private final String qualifier;
    private final Set<String> dependencies;
    private final Constructor<T> constructor;

    public NodeImpl(String qualifier,
                    Set<String> dependencies,
                    Constructor<T> constructor) {

        this.qualifier    = requireNonNull(qualifier);
        this.dependencies = requireNonNull(dependencies);
        this.constructor  = requireNonNull(constructor);
    }

    @Override
    public String getQualifier() {
        return qualifier;
    }

    @Override
    public Set<String> getDependencies() {
        return dependencies;
    }

    @Override
    public T instantiate(Map<String, Object> dependencies) {
        constructor.setAccessible(true);

        final Object[] values = Stream.of(constructor.getParameters())
            .map(p -> dependencies.get(
                ofNullable(p.getAnnotation(Inject.class))
                    .map(Inject::value)
                    .orElseGet(() -> p.getType().getName())
            )).toArray();

        try {
            final T instance = constructor.newInstance(values);
            traverseFields(instance.getClass())
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .forEach(f -> {
                    final String qualifier =
                        of(f.getAnnotation(Inject.class).value())
                            .filter(s -> !s.isEmpty())
                            .orElseGet(() -> f.getType().getName());

                    try {
                        f.setAccessible(true);
                        switch (f.getType().getName()) {
                            case "long"    : f.setLong(instance, (Long) dependencies.get(qualifier)); break;
                            case "int"     : f.setInt(instance, (Integer) dependencies.get(qualifier)); break;
                            case "short"   : f.setShort(instance, (Short) dependencies.get(qualifier)); break;
                            case "byte"    : f.setByte(instance, (Byte) dependencies.get(qualifier)); break;
                            case "double"  : f.setDouble(instance, (Double) dependencies.get(qualifier)); break;
                            case "float"   : f.setFloat(instance, (Float) dependencies.get(qualifier)); break;
                            case "char"    : f.setChar(instance, (Character) dependencies.get(qualifier)); break;
                            case "boolean" : f.setBoolean(instance, (Boolean) dependencies.get(qualifier)); break;
                            default        : f.set(instance, dependencies.get(qualifier));
                        }
                    } catch (final IllegalAccessException ex) {
                        throw new InjectorException(
                            "Failed to inject member variable " + f.getName() +
                            " of type " + f.getType().getName() +
                            " with qualifier " + qualifier +
                            " in class " + instance.getClass().getName() + "."
                        );
                    }
                });

            return instance;
        } catch (final IllegalAccessException
                     | InvocationTargetException
                     | InstantiationException ex) {

            throw new InjectorException(
                "Failed to instantiate injectable type with qualifier " +
                qualifier + " using constructor with parameters (" +
                Stream.of(constructor.getParameters())
                    .map(Parameter::getType)
                    .map(Class::getName)
                    .collect(joining(", ")) +
                ") given the values (" +
                Stream.of(values)
                    .map(Object::toString)
                    .collect(joining(", ")) +
                ").", ex
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        final Node<?> that = (Node<?>) o;
        return qualifier.equals(that.getQualifier())
            && dependencies.equals(that.getDependencies());
    }

    @Override
    public int hashCode() {
        int result = qualifier.hashCode();
        result = 31 * result + dependencies.hashCode();
        return result;
    }
}