/**
 * The main package for the dependency injector interfaces. The dependency
 * injection system follows a builder pattern where the two interfaces
 * {@link com.github.pyknic.stiletto.Injector} and
 * {@link com.github.pyknic.stiletto.InjectorBuilder} are central.
 * <p>
 * All the interfaces and classes in this package are part of the public API and
 * will therefore only be modified as part of a new major version of the
 * library. Implementations are located in an internal package that might be
 * modified between major releases.
 * <p>
 * <h3>Configuration</h3>
 * The builder pattern allows the dependency injector to be built by appending
 * types available for dependency injection. An optional qualifier string can be
 * specified to help identify different implementations. If the dependency graph
 * is incomplete or contains cyclic dependencies when the
 * {@link com.github.pyknic.stiletto.InjectorBuilder#build()}-method is invoked,
 * then an {@link com.github.pyknic.stiletto.InjectorException} is thrown.
 *
 * {@code
 *     // Create a new dependency injector with a number of injectable
 *     // implementations.
 *     Injector injector = Injector.builder()
 *         .withType(CommentComponentImpl.class)
 *         .withType(TopicComponentImpl.class)
 *         .withType(MySQLComponentImpl.class, "mysql")
 *         .withType(PostgreSQLComponentImpl.class, "postgreSQL")
 *         .build();
 * }
 *
 * <h3>Annotation Injection</h3>
 * Fields and/or constructors can be annotated to direct the dependency
 * injector. A qualifier string can be specified in the annotation to control
 * which instance is loaded if there are multiple implementations of the same
 * interface.
 *
 * {@code
 *     class TopicComponentImpl implements TopicComponent {
 *
 *         private final CommentComponent comments;
 *
 *         @Inject("mysql")
 *         private DatabaseComponent database;
 *
 *         // Don't use this constructor
 *         TopicComponentImpl() {
 *             comments = new DummyCommentComponent();
 *         }
 *
 *         // Instead, use this constructor:
 *         @Inject
 *         TopicComponentImpl(CommentComponent comments) {
 *             this.comments = requireNonNull(comments);
 *         }
 *
 *     }
 * }
 *
 * <h3>Programmatic Injection</h3>
 * The {@link com.github.pyknic.stiletto.Injector} instance can also be invoked
 * programmatically to obtain instances from the graph.
 *
 * {@code
 *     // Get a type based on the interface (or the implementation)
 *     final CommentComponent comments =
 *         injector.getOrThrow(CommentComponent.class);
 *
 *     // Get an instance if one is available
 *     final Optional<OtherComponent> optional =
 *         injector.get(OtherComponent.class);
 *
 *     // See if an implementation for a particular interface is available.
 *     final boolean hasTopicComponent =
 *         injector.has(TopicComponent.class);
 * }
 *
 *
 */
package com.github.pyknic.stiletto;