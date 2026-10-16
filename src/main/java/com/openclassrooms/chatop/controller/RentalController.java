package com.openclassrooms.chatop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.chatop.dto.RentalResponse;
import com.openclassrooms.chatop.dto.RentalsResponse;
import com.openclassrooms.chatop.service.RentalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rentals")
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
}
