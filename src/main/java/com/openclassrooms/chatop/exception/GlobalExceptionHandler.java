package com.openclassrooms.chatop.exception;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Les corps d'erreur reprennent ceux de Mockoon : {} pour un 400, {"message": "error"} pour un 401.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<Map<String, String>> handleForbidden() {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleNotFound() {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of());
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<Map<String, String>> handleInvalidCredentials() {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "error"));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<Map<String, String>> handlePayloadTooLarge() {
		return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(Map.of());
	}

	@ExceptionHandler({
			EmailAlreadyUsedException.class,
			InvalidFileException.class,
			BindException.class,
			MethodArgumentNotValidException.class,
			HttpMessageNotReadableException.class,
			// Course entre deux inscriptions avec le même email : l'index unique en base tranche
			DataIntegrityViolationException.class })
	public ResponseEntity<Map<String, String>> handleBadRequest() {
		return ResponseEntity.badRequest().body(Map.of());
	}
}
