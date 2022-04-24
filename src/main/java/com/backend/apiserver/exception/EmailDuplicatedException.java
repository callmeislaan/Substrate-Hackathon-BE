package com.backend.apiserver.exception;

public class EmailDuplicatedException extends DataDuplicatedException {
	public EmailDuplicatedException() {
	}
	public EmailDuplicatedException(final String message) {
		super(message);
	}
}
