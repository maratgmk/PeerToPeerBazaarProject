package org.gafiev.peertopeerbazaar.exception;

import java.util.Map;
/**
 * Exception thrown when a requested database entity or domain object cannot be found.
 *
 * This exception is typically used in the service or repository layer to indicate
 * that a lookup operation failed to return a result. It provides a convenient
 * constructor to automatically format an error message based on the entity class
 * and the parameters used for the search.
 *
 * As a RuntimeException, it is unchecked, allowing for clean
 * service interfaces without mandatory error handling at every layer.
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception by automatically formatting a message with
     * the entity type and the search criteria.
     *
     * @param type the Class of the entity that was not found.
     * @param queryParams a Map containing the search parameters (e.g., "id" -> "123")
     *                    that resulted in no entity being found.
     */
    public EntityNotFoundException(Class<?> type, Map<String, String> queryParams) {
        this("Can't find entity %s with params: %s".formatted(type.getName(), queryParams));
    }

    /**
     * Constructs a new exception with a specific detail message.
     *
     * @param message the detail message which is saved for later retrieval
     *                by the getMessage() method.
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}
