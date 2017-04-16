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
