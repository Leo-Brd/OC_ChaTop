package com.openclassrooms.chatop.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MessageResponse(
		@Schema(description = "Confirmation message", example = "Message send with success")
		String message) {
}
