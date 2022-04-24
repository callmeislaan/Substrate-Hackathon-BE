package com.backend.apiserver.exception;

public class RequestAnnouncementNotFoundException extends NotFoundException {
	public RequestAnnouncementNotFoundException() {
	}

	public RequestAnnouncementNotFoundException(final String message) {
		super(message);
	}
}
