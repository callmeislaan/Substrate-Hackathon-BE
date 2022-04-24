package com.backend.apiserver.exception;

public class SkillNotFoundException extends NotFoundException {
	public SkillNotFoundException() {
	}

	public SkillNotFoundException(final String message) {
		super(message);
	}
}