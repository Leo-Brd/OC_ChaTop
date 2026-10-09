package com.openclassrooms.chatop.exception;

public class EmailAlreadyUsedException extends RuntimeException {

	public EmailAlreadyUsedException() {
		super("Email already used");
	}
}
