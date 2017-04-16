package com.github.pyknic.stiletto;

/**
 * Builder for the {@link Injector} class. To create an instance of this
 * interface using the default implementation, use {@link Injector#builder()}.
 *
 * @author Emil Forslund
 * @since 1.0.0
 */
public interface InjectorBuilder {

    /**
     * Adds a type to the injector being built so that it and all its ancestors
     * can be dependency injected.
     *
     * @see #withType(Class)
     *
     * @param clazz  the class to be injectable
     * @return       this builder
     */
    <T> InjectorBuilder withType(Class<T> clazz, String qualifier);

    /**
     * Adds a type to the injector being built so that it and all its ancestors
     * can be dependency injected. If the specified class has a
     * {@link Inject}-annotation, then the {@link Inject#value()} will be used
     * as qualifier. If it doesn't have the annotation, or if the value is
     * empty, then a qualifier will be generated using the name of the
     * {@link Class#getName()}.
     *
     * @see #withType(Class, String)
     *
     * @param clazz  the class to be injectable
     * @return       this builder
     */
    default <T> InjectorBuilder withType(Class<T> clazz) {
        return withType(clazz, clazz.getName());
    }

    /**
     * Builds the dependency injector, rendering it immutable. This builder
     * should <em>not</em> be used beyond this point.
     *
     * @return  the built instance
     */
    Injector build();

}