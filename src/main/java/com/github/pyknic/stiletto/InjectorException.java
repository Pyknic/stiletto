package com.github.pyknic.stiletto;

/**
 * Exception thrown if dependency injection can't be completed.
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class InjectorException extends RuntimeException {

    private static final long serialVersionUID = -5986130455120347787L;

    /**
     * Creates a new exception with a message.
     *
     * @param message  the message
     */
    public InjectorException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with a message and a cause.
     *
     * @param message  the message
     * @param cause    the cause
     */
    public InjectorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception that states that the specified type has not been
     * installed in the injector.
     *
     * @param type  the type
     * @return      the exception
     */
    static InjectorException unknownTypeException(Class<?> type) {
        return new InjectorException(
            "Type '" + type + "' has not been installed in injector."
        );
    }

    /**
     * Creates a new exception that states that the specified qualifier has not
     * been installed in the injector.
     *
     * @param qualifier  the qualifier
     * @return           the exception
     */
    static InjectorException unknownQualifierException(String qualifier) {
        return new InjectorException(
            "Qualifier '" + qualifier + "' has not been installed in injector."
        );
    }
}