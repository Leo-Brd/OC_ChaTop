package com.openclassrooms.chatop.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RentalCreateRequest(
		@Schema(example = "Seaside house") @NotBlank @Size(max = 255) String name,
		@Schema(description = "Surface in square meters", example = "80") @NotNull @Positive BigDecimal surface,
		@Schema(description = "Price per night", example = "600") @NotNull @Positive BigDecimal price,
		@Schema(example = "Quiet house, 5 minutes from the beach") @NotBlank @Size(max = 2000) String description,
		@Schema(description = "Picture file (jpg, jpeg, png, gif or webp, 5 MB max)", type = "string", format = "binary")
		@NotNull MultipartFile picture) {
}
