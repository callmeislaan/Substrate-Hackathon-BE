package com.backend.apiserver.exception;

public class UserNotFoundException extends NotFoundException {
	public UserNotFoundException() {
	}

	public UserNotFoundException(final String message) {
		super(message);
	}
}
