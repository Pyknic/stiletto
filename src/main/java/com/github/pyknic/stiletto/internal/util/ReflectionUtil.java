package com.github.pyknic.stiletto.internal.util;

import java.lang.reflect.Field;
import java.util.stream.Stream;

/**
 * Some common utility methods for analyzing classes with reflection.
 *
 * @author  Emil Forslund
 * @since   1.0.0
 */
public final class ReflectionUtil {

    public static Stream<Field> traverseFields(Class<?> clazz) {
        final Class<?> parent = clazz.getSuperclass();
        final Stream<Field> inherited;

        if (parent != null) {
            inherited = traverseFields(parent);
        } else {
            inherited = Stream.empty();
        }

        return Stream.concat(inherited, Stream.of(clazz.getDeclaredFields()));
    }

    public static Stream<Class<?>> traverseAncestors(Class<?> clazz) {
        if (clazz.getSuperclass() == null) { // We have reached Object.class
            return Stream.of(clazz);
        } else {
            return Stream.concat(
                Stream.of(clazz),
                Stream.concat(
                    traverseAncestors(clazz.getSuperclass()),
                    Stream.of(clazz.getInterfaces())
                )
            ).distinct();
        }
    }

    private ReflectionUtil() {}
}