package com.openclassrooms.chatop.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.chatop.dto.LoginRequest;
import com.openclassrooms.chatop.dto.RegisterRequest;
import com.openclassrooms.chatop.exception.EmailAlreadyUsedException;
import com.openclassrooms.chatop.exception.InvalidCredentialsException;
import com.openclassrooms.chatop.model.User;
import com.openclassrooms.chatop.repository.UserRepository;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public String register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new EmailAlreadyUsedException();
		}

		User user = new User();
		user.setName(request.name());
		user.setEmail(request.email());
		user.setPassword(passwordEncoder.encode(request.password()));
		userRepository.save(user);

		return jwtService.generateToken(user.getEmail());
	}

	@Transactional(readOnly = true)
	public String login(LoginRequest request) {
		User user = userRepository.findByEmail(request.email())
				.filter(found -> passwordEncoder.matches(request.password(), found.getPassword()))
				.orElseThrow(InvalidCredentialsException::new);

		return jwtService.generateToken(user.getEmail());
	}
}
