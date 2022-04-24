package com.backend.apiserver.exception;

public class MentorRequestNotFoundException extends NotFoundException{
	public MentorRequestNotFoundException() {
	}

	public MentorRequestNotFoundException(final String message) {
		super(message);
	}
}
