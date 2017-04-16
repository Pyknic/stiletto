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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotates that the specified element should be selected for dependency
 * injection, potentially under a specified qualifier.
 *
 * <h3>Element Types</h3>
 * If placed on an {@link ElementType#TYPE type}, then this has no other effect
 * than to mark the class as dependency managed for other programmers. If placed
 * on an {@link ElementType#CONSTRUCTOR constructor}, then that constructor will
 * be used when creating instances of the class. If no constructor is marked
 * then any constructor might be selected. If placed on a
 * {@link ElementType#CONSTRUCTOR parameter} or
 * {@link ElementType#CONSTRUCTOR field}, then it has no other effect than to
 * indicate which qualifier applies to that field.
 *
 * <h3>Qualifiers</h3>
 * Normally, the qualifier for an injected value is the absolute class name.
 * However, a custom qualifier can be specified by using the {@link #value()} on
 * this annotation.
 *
 * @author Emil Forslund
 * @since 1.0.0
 */
@Retention(RUNTIME)
@Target({TYPE, CONSTRUCTOR, PARAMETER, FIELD})
public @interface Inject {

    /**
     * Qualifier for the injected value.
     *
     * @return  the qualifier
     */
    String value() default "";

}
