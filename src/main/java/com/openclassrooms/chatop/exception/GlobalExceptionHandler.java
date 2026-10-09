package com.openclassrooms.chatop.exception;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Les corps d'erreur reprennent ceux de Mockoon : {} pour un 400, {"message": "error"} pour un 401.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<Map<String, String>> handleInvalidCredentials() {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "error"));
	}

	@ExceptionHandler({
			EmailAlreadyUsedException.class,
			MethodArgumentNotValidException.class,
			HttpMessageNotReadableException.class,
			// Course entre deux inscriptions avec le même email : l'index unique en base tranche
			DataIntegrityViolationException.class })
	public ResponseEntity<Map<String, String>> handleBadRequest() {
		return ResponseEntity.badRequest().body(Map.of());
	}
}
