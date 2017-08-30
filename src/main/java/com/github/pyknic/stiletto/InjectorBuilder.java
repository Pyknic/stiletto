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
package com.github.pyknic.stiletto;

/**
 * Builder for the {@link Injector} class. To create an instance of this
 * interface using the default implementation, use {@link Injector#builder()}.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public interface InjectorBuilder {

    /**
     * Adds a type to the injector being built so that it and all its ancestors
     * can be dependency injected. An optional qualifiers can be specified to
     * keep multiple implementations of the same interface apart. If not
     * specified, then the absolute class name is used.
     *
     * @see #withType(Class)
     *
     * @param <T>        the injectable (implementation) type
     * @param clazz      the class to be injectable
     * @param qualifier  qualifier for the implementation
     * @return           this builder
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
     * @param <T>    the injectable (implementation) type
     * @param clazz  the class to be injectable
     * @return       this builder
     */
    default <T> InjectorBuilder withType(Class<T> clazz) {
        return withType(clazz, clazz.getName());
    }
    
    /**
     * Adds all types that are annotated with the {@link Provider} annotation to
     * the injector being built, so that them and all of their ancestors can be
     * dependency injected. Each type found by recursively scanning the class
     * path from the parameter {@code scanSpec}, 
     * <a href="https://github.com/lukehutch/fast-classpath-scanner/wiki/2.-Constructor#scan-spec">
     * using these semantics</a>, are added to the injector via the 
     * {@link #withType(Class, String)} method.
     * <p>
     * For simple usage, you may leave the {@code scanSpec} parameter empty
     *
     * @param scanSpec  the scan specifications
     * @return          this builder
     */
    InjectorBuilder fromProviders(String... scanSpec);

    /**
     * Builds the dependency injector, rendering it immutable. This builder
     * should <em>not</em> be used beyond this point.
     *
     * @return  the built instance
     */
    Injector build();

}
