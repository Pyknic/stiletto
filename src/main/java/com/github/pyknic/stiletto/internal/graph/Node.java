package com.github.pyknic.stiletto.internal.graph;

import java.util.Map;
import java.util.Set;

/**
 * An injectable node with a set of {@link #getDependencies() dependencies}.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public interface Node<T> {

    /**
     * Qualifier used to identify a specific node.
     *
     * @return  the qualifier
     */
    String getQualifier();

    /**
     * Qualifiers for the dependencies that are needed to create this instance.
     *
     * @return  set of dependencies
     */
    Set<String> getDependencies();

    /**
     * Create a new instance of the represented type, using the specified map
     * of already created instances to inject any dependencies.
     *
     * @param dependencies  already injected instances
     * @return              the created instance
     */
    T instantiate(Map<String, Object> dependencies);

}