package com.ecotracker.ecotrackerbackend.exceptions;

import java.util.List;

public class ValidationException extends RuntimeException {
    private List<String> errorsMessages;

    public ValidationException(List<String> errorsMessages) {
        super("il payload contiene errori");
        this.errorsMessages = errorsMessages;
    }

    public List<String> getErrorsMessages() { return errorsMessages; }
}
