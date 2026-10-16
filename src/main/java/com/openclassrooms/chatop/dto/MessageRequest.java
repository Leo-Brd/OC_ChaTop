package com.openclassrooms.chatop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MessageRequest(
		@Schema(description = "Id of the rental the message is about", example = "1")
		@NotNull @JsonProperty("rental_id") Integer rentalId,
		@Schema(description = "Id of the sender, must be the authenticated user", example = "2")
		@NotNull @JsonProperty("user_id") Integer userId,
		@Schema(description = "Content of the message", example = "Is the house available in August?")
		@NotBlank @Size(max = 2000) String message) {
}
