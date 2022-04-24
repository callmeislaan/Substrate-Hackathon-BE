package com.backend.apiserver.exception;

public class PasswordNotMatchException extends Exception {
    public PasswordNotMatchException() {
    }

    public PasswordNotMatchException(final String message) {
        super(message);
    }
}
