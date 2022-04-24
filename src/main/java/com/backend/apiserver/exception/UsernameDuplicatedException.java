package com.backend.apiserver.exception;

public class UsernameDuplicatedException extends DataDuplicatedException {
	public UsernameDuplicatedException() {
	}
	public UsernameDuplicatedException(final String message) {
		super(message);
	}
}
