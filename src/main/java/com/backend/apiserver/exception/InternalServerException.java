package com.backend.apiserver.exception;

public class InternalServerException extends Exception {

    public InternalServerException() {
    }

    public InternalServerException(final String message) {
        super(message);
    }

}
