package com.openclassrooms.chatop.service;

import org.springframework.stereotype.Service;

import com.openclassrooms.chatop.dto.UserResponse;
import com.openclassrooms.chatop.exception.InvalidCredentialsException;
import com.openclassrooms.chatop.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserResponse getByEmail(String email) {
		return userRepository.findByEmail(email)
				.map(UserResponse::from)
				// Token valide mais compte supprimé depuis
				.orElseThrow(InvalidCredentialsException::new);
	}
}
