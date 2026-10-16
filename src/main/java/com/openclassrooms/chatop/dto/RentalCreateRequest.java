package com.openclassrooms.chatop.dto;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RentalCreateRequest(
		@NotBlank @Size(max = 255) String name,
		@NotNull @Positive BigDecimal surface,
		@NotNull @Positive BigDecimal price,
		@NotBlank @Size(max = 2000) String description,
		@NotNull MultipartFile picture) {
}
