package com.openclassrooms.chatop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Size(max = 255) String name,
		@NotBlank @Email @Size(max = 255) String email,
		// 72 octets : limite de BCrypt
		@NotBlank @Size(max = 72) String password) {
}
