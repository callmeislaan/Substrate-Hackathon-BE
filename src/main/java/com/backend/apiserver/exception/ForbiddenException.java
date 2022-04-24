package com.backend.apiserver.exception;

import lombok.Getter;

@Getter
public class ForbiddenException extends Exception {

    private String code;

    public ForbiddenException() {
    }

    public ForbiddenException(final String message) {
        super(message);
    }

}
