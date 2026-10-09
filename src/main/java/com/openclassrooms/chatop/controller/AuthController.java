package com.openclassrooms.chatop.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.chatop.dto.AuthResponse;
import com.openclassrooms.chatop.dto.LoginRequest;
import com.openclassrooms.chatop.dto.RegisterRequest;
import com.openclassrooms.chatop.dto.UserResponse;
import com.openclassrooms.chatop.service.AuthService;
import com.openclassrooms.chatop.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {

	private final AuthService authService;
	private final UserService userService;

	public AuthController(AuthService authService, UserService userService) {
		this.authService = authService;
		this.userService = userService;
	}

	@PostMapping("/register")
	@Operation(summary = "Create an account and get a JWT")
	@ApiResponse(responseCode = "200", description = "Account created")
	@ApiResponse(responseCode = "400", description = "Invalid body or email already used")
	public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
		return new AuthResponse(authService.register(request));
	}

	@PostMapping("/login")
	@Operation(summary = "Log in and get a JWT")
	@ApiResponse(responseCode = "200", description = "Authenticated")
	@ApiResponse(responseCode = "401", description = "Invalid credentials")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return new AuthResponse(authService.login(request));
	}

	@GetMapping("/me")
	@Operation(summary = "Get the authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponse(responseCode = "200", description = "Current user")
	@ApiResponse(responseCode = "401", description = "Missing or invalid token")
	public UserResponse me(@AuthenticationPrincipal Jwt jwt) {
		return userService.getByEmail(jwt.getSubject());
	}
}
