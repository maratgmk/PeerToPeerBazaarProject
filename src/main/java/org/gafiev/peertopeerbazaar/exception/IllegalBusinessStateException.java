package org.gafiev.peertopeerbazaar.exception;

/**
 * Exception thrown when the application is in an inappropriate state for
 * the requested business operation.
 *
 * This exception indicates that a business rule has been violated due to
 * the current state of the domain model. Unlike IllegalStateException,
 * which is typically used for technical or low-level state errors, this class
 * is specifically intended for high-level business logic inconsistencies.
 *
 * As an unchecked exception, it does not require explicit
 * declaration in method signatures or mandatory try-catch blocks.
 */
public class IllegalBusinessStateException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message describing
     * the business state violation.
     *
     * @param message the detail message which is saved for later retrieval
     *                by the getMessage() method.
     */
    public IllegalBusinessStateException(String message) {
        super(message);
    }
}

