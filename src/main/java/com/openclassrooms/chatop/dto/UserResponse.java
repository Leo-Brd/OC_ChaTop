package com.openclassrooms.chatop.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openclassrooms.chatop.model.User;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(
		@Schema(example = "1") Integer id,
		@Schema(example = "Alice Martin") String name,
		@Schema(example = "alice@example.com") String email,
		@Schema(example = "2026-10-16T11:55:00") @JsonProperty("created_at") LocalDateTime createdAt,
		@Schema(example = "2026-10-16T11:55:00") @JsonProperty("updated_at") LocalDateTime updatedAt) {

	public static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
	}
}
