package org.gafiev.peertopeerbazaar.exception;

import java.util.Map;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> type, Map<String, String> queryParams) {
        super("Can't find entity %s with params: %s".formatted(type.getName(), queryParams));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
