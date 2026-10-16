package com.openclassrooms.chatop.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
		@Schema(description = "JWT to send in the Authorization header as 'Bearer <token>'", example = "eyJhbGciOiJIUzI1NiJ9...")
		String token) {
}
