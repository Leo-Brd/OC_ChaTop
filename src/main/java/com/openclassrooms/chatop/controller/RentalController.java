package com.openclassrooms.chatop.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.chatop.dto.MessageResponse;
import com.openclassrooms.chatop.dto.RentalCreateRequest;
import com.openclassrooms.chatop.dto.RentalResponse;
import com.openclassrooms.chatop.dto.RentalUpdateRequest;
import com.openclassrooms.chatop.dto.RentalsResponse;
import com.openclassrooms.chatop.service.RentalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rentals", description = "Rentals listing, details, creation and update")
@SecurityRequirement(name = "bearerAuth")
public class RentalController {

	private final RentalService rentalService;

	public RentalController(RentalService rentalService) {
		this.rentalService = rentalService;
	}

	@GetMapping
	@Operation(summary = "List all rentals")
	@ApiResponse(responseCode = "200", description = "All rentals")
	@ApiResponse(responseCode = "401", description = "Missing or invalid token")
	public RentalsResponse findAll() {
		return rentalService.findAll();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a rental by id")
	@ApiResponse(responseCode = "200", description = "The rental")
	@ApiResponse(responseCode = "401", description = "Missing or invalid token")
	@ApiResponse(responseCode = "404", description = "Rental not found")
	public RentalResponse findById(@PathVariable Integer id) {
		return rentalService.findById(id);
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Create a rental with its picture (multipart/form-data)")
	@ApiResponse(responseCode = "200", description = "Rental created")
	@ApiResponse(responseCode = "400", description = "Invalid fields or picture")
	@ApiResponse(responseCode = "401", description = "Missing or invalid token")
	public MessageResponse create(@Valid @ModelAttribute RentalCreateRequest request, @AuthenticationPrincipal Jwt jwt) {
		return rentalService.create(request, jwt.getSubject());
	}

	@PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Update a rental (multipart/form-data)")
	@ApiResponse(responseCode = "200", description = "Rental updated")
	@ApiResponse(responseCode = "400", description = "Invalid fields")
	@ApiResponse(responseCode = "401", description = "Missing or invalid token")
	@ApiResponse(responseCode = "403", description = "Not the owner of the rental")
	@ApiResponse(responseCode = "404", description = "Rental not found")
	public MessageResponse update(@PathVariable Integer id, @Valid @ModelAttribute RentalUpdateRequest request,
			@AuthenticationPrincipal Jwt jwt) {
		return rentalService.update(id, request, jwt.getSubject());
	}
}
