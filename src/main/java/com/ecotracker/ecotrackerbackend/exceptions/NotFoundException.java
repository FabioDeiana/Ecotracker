package com.ecotracker.ecotrackerbackend.exceptions;

public class NotFoundException extends RuntimeException {


    public NotFoundException(String message) {
        super(message);
    }
}
