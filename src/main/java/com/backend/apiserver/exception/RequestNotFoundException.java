package com.backend.apiserver.exception;

public class RequestNotFoundException extends NotFoundException {
	public RequestNotFoundException() {
	}

	public RequestNotFoundException(final String message) {
		super(message);
	}
}
