package com.openclassrooms.chatop.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.chatop.dto.MessageRequest;
import com.openclassrooms.chatop.dto.MessageResponse;
import com.openclassrooms.chatop.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

	private final MessageService messageService;

	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}

	@PostMapping
	@Operation(summary = "Send a message about a rental")
	@ApiResponse(responseCode = "200", description = "Message sent")
	@ApiResponse(responseCode = "400", description = "Invalid body")
	@ApiResponse(responseCode = "401", description = "Missing or invalid token")
	@ApiResponse(responseCode = "403", description = "user_id differs from the authenticated user")
	@ApiResponse(responseCode = "404", description = "Rental not found")
	public MessageResponse send(@Valid @RequestBody MessageRequest request, @AuthenticationPrincipal Jwt jwt) {
		return messageService.send(request, jwt.getSubject());
	}
}
