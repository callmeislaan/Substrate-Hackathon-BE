package com.backend.apiserver.entity;

public enum Status {
	/**
	 * Common status for all entity
	 */
	ACTIVE, DELETE,

	/**
	 * Status for request operations
	 */
	INVITE, OPEN, PENDING, DOING, CONFLICT, COMPLETE, REJECT,

	/**
	 * Status for used anest card
	 */
	USED
}
