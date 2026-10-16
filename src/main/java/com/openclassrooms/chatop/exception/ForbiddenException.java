package com.openclassrooms.chatop.exception;

public class ForbiddenException extends RuntimeException {

	public ForbiddenException() {
		super("Forbidden");
	}
}
