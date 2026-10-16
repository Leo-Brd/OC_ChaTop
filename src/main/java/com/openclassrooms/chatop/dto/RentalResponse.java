package com.openclassrooms.chatop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openclassrooms.chatop.model.Rental;

import io.swagger.v3.oas.annotations.media.Schema;

public record RentalResponse(
		@Schema(example = "1") Integer id,
		@Schema(example = "Seaside house") String name,
		@Schema(description = "Surface in square meters", example = "80") BigDecimal surface,
		@Schema(description = "Price per night", example = "600") BigDecimal price,
		@Schema(description = "Public URL of the picture", example = "http://localhost:3001/images/0b8d5c1e.png") String picture,
		@Schema(example = "Quiet house, 5 minutes from the beach") String description,
		@Schema(description = "Id of the owner", example = "1") @JsonProperty("owner_id") Integer ownerId,
		@Schema(example = "2026-10-16T11:55:00") @JsonProperty("created_at") LocalDateTime createdAt,
		@Schema(example = "2026-10-16T11:55:00") @JsonProperty("updated_at") LocalDateTime updatedAt) {

	public static RentalResponse from(Rental rental) {
		return new RentalResponse(
				rental.getId(),
				rental.getName(),
				rental.getSurface(),
				rental.getPrice(),
				rental.getPicture(),
				rental.getDescription(),
				rental.getOwner().getId(),
				rental.getCreatedAt(),
				rental.getUpdatedAt());
	}
}
