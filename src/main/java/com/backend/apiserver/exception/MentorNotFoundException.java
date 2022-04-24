package com.backend.apiserver.exception;

public class MentorNotFoundException extends NotFoundException {
	public MentorNotFoundException() {
	}

	public MentorNotFoundException(final String message) {
		super(message);
	}
}
