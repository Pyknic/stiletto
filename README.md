# stiletto
[![Build Status](https://travis-ci.org/Pyknic/stiletto.svg?branch=master)](https://travis-ci.org/Pyknic/stiletto)
[![Javadocs](http://javadoc.io/badge/com.github.pyknic/stiletto.svg)](http://javadoc.io/doc/com.github.pyknic/stiletto)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.pyknic/stiletto/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.pyknic/stiletto)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Dependency Injection Library for Java that uses constructor invocation to support immutable instances.

## Features
* Minimal Overhead
* `final` members
* Non-default constructors
* Interface/Implementation separation
* Named Instances using Qualifiers
* No classpath scanning

## Installation

#### Using Maven
Add the following to your `pom.xml`-file:

```xml
<dependency>
    <groupId>com.github.pyknic</groupId>
    <artifactId>stiletto</artifactId>
    <version>1.0.3</version>
</dependency>
```

#### Using Gradle
Add the following to your `build.gradle`-file:
```gradle
compile group: 'com.github.pyknic', name: 'stiletto', version: '1.0.3'
```

## Usage
The main package for the dependency injector interfaces. The dependency injection system follows a builder pattern where the two interfaces `Injector` and `InjectorBuilder` are central.

### Configuration
The builder pattern allows the dependency injector to be built by appending types available for dependency injection. An optional qualifier string can be specified to help identify different implementations. If the dependency graph is incomplete or contains cyclic dependencies when the `InjectorBuilder.build()`-method is invoked, then an `InjectorException` is thrown.

```java
// Create a new dependency injector with a number of injectable
// implementations.
Injector injector = Injector.builder()
    .withType(CommentComponentImpl.class)
    .withType(TopicComponentImpl.class)
    .withType(MySQLComponentImpl.class, "mysql")
    .withType(PostgreSQLComponentImpl.class, "postgreSQL")
    .build();
```

### Annotation Injection
Fields and/or constructors can be annotated to direct the dependency injector. A qualifier string can be specified in the annotation to control which instance is loaded if there are multiple implementations of the same interface.

```java
// Custom class
class TopicComponentImpl implements TopicComponent {

    private final CommentComponent comments;

    @Inject("mysql")
    private DatabaseComponent database;

    // Don't use this constructor
    TopicComponentImpl() {
        comments = new DummyCommentComponent();
    }

    // Instead, use this constructor:
    @Inject
    TopicComponentImpl(CommentComponent comments) {
        this.comments = requireNonNull(comments);
    }
}
```

### Programmatic Injection
The `Injector` instance can also be invoked programmatically to obtain instances from the graph.

```java
// Get a type based on the interface (or the implementation)
final CommentComponent comments =
    injector.getOrThrow(CommentComponent.class);

// Get an instance if one is available
final Optional<OtherComponent> optional =
    injector.get(OtherComponent.class);

// See if an implementation for a particular interface is available.
final boolean hasTopicComponent =
    injector.has(TopicComponent.class);
```

## License
Copyright 2017 Emil Forslund

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
