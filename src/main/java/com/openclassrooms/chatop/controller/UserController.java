package com.openclassrooms.chatop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.chatop.dto.UserResponse;
import com.openclassrooms.chatop.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a user by id")
	@ApiResponse(responseCode = "200", description = "The user")
	@ApiResponse(responseCode = "401", description = "Missing or invalid token")
	@ApiResponse(responseCode = "404", description = "User not found")
	public UserResponse findById(@PathVariable Integer id) {
		return userService.getById(id);
	}
}
