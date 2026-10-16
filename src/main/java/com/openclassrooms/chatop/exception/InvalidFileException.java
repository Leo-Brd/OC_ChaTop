package com.openclassrooms.chatop.exception;

public class InvalidFileException extends RuntimeException {

	public InvalidFileException() {
		super("Invalid file");
	}
}
