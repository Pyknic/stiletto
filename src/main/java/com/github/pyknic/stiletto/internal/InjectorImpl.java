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