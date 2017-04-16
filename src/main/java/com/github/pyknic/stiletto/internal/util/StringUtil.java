package com.github.pyknic.stiletto.internal.util;

/**
 * Utility class for operating on strings.
 *
 * @author Emil Forslund
 * @since 1.0.0
 */
public final class StringUtil {

    /**
     * Returns {@code true} if the specified string is not {@code null} and not
     * an empty string.
     *
     * @param str  the string to test
     * @return     {@code true} if non-null and not-empty, else {@code false}
     */
    public static boolean notEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * Should never be invoked.
     */
    private StringUtil() {}

}
