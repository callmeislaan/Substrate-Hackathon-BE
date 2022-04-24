package com.backend.apiserver.exception;

public class RoleNotFoundException extends NotFoundException {
	public RoleNotFoundException() {
	}

	public RoleNotFoundException(final String message) {
		super(message);
	}
}
