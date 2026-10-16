package com.openclassrooms.chatop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@Schema(description = "Display name", example = "Alice Martin")
		@NotBlank @Size(max = 255) String name,
		@Schema(description = "Unique email, used to log in", example = "alice@example.com")
		@NotBlank @Email @Size(max = 255) String email,
		// 72 octets : limite de BCrypt
		@Schema(description = "Password, stored hashed with BCrypt", example = "MyS3cretPassw0rd")
		@NotBlank @Size(max = 72) String password) {
}
