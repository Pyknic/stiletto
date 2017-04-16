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

    /**
     * Returns a stream of all the member fields for the specified class,
     * including inherited fields from any ancestors. This includes public,
     * private, protected and package private fields.
     *
     * @param clazz  the class to traverse
     * @return       stream of fields
     */
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

    /**
     * Returns a stream of all the classes upwards in the inheritance tree of
     * the specified class, including the class specified as the first element
     * and {@code java.lang.Object} as the last one.
     *
     * @param clazz  the first class in the tree
     * @return       stream of ancestors (including {@code clazz})
     */
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

    /**
     * Should never be invoked.
     */
    private ReflectionUtil() {}
}