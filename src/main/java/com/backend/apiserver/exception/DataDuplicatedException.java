package com.backend.apiserver.exception;

public class DataDuplicatedException extends Exception {

    public DataDuplicatedException() {
    }

    public DataDuplicatedException(final String message) {
        super(message);
    }

}
