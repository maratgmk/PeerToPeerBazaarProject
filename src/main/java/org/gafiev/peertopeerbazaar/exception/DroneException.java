package org.gafiev.peertopeerbazaar.exception;

/**
 * Base exception class for all errors occurring within the drone management system.
 *
 * This class serves as a general-purpose runtime exception for drone-related
 * failures, such as communication loss, navigation errors, or hardware malfunctions.
 * More specific exceptions (e.g., BatteryLowException should extend this class.
 *
 * As an unchecked exception, it simplifies error propagation across
 * flight control layers without cluttering method signatures.<
 */
public class DroneException extends RuntimeException {

    /**
     * Constructs a new drone exception with the specified detail message.
     *
     * @param message the detail message describing the drone failure.
     */
    public DroneException(String message) {
        super(message);
    }

    /**
     * Constructs a new drone exception with the specified detail message and
     * a cause (wrapping another exception).
     *
     * This constructor is particularly useful for exception chaining,
     * for instance, when a low-level protocol error (like  java.io.IOException)
     * is translated into a higher-level drone failure.
     *
     * @param message the detail message describing the context of the error.
     * @param cause the underlying cause of the failure (e.g., a hardware driver error).
     */
    public DroneException(String message, Throwable cause) {
        super(message, cause);
    }
}
