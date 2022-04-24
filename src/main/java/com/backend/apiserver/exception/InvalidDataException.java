package com.backend.apiserver.exception;

public class InvalidDataException extends Exception {
	public InvalidDataException() {
	}

	public InvalidDataException(final String message) {
		super(message);
	}
}
