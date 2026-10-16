package com.openclassrooms.chatop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
		@Schema(description = "Email of the account", example = "alice@example.com")
		@NotBlank @Size(max = 255) String email,
		@Schema(description = "Password of the account", example = "MyS3cretPassw0rd")
		@NotBlank @Size(max = 72) String password) {
}
