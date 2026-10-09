package com.openclassrooms.chatop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.chatop.dto.LoginRequest;
import com.openclassrooms.chatop.dto.RegisterRequest;
import com.openclassrooms.chatop.exception.EmailAlreadyUsedException;
import com.openclassrooms.chatop.exception.InvalidCredentialsException;
import com.openclassrooms.chatop.model.User;
import com.openclassrooms.chatop.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtService jwtService;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	private AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthService(userRepository, passwordEncoder, jwtService);
	}

	@Test
	void registerStoresAnEncodedPasswordAndReturnsAToken() {
		when(userRepository.existsByEmail("a@b.com")).thenReturn(false);
		when(jwtService.generateToken("a@b.com")).thenReturn("jwt");

		String token = authService.register(new RegisterRequest("Alice", "a@b.com", "secret123"));

		ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(saved.capture());
		assertThat(token).isEqualTo("jwt");
		assertThat(saved.getValue().getPassword()).isNotEqualTo("secret123");
		assertThat(passwordEncoder.matches("secret123", saved.getValue().getPassword())).isTrue();
	}

	@Test
	void registerRejectsAnEmailAlreadyUsed() {
		when(userRepository.existsByEmail("a@b.com")).thenReturn(true);

		assertThatThrownBy(() -> authService.register(new RegisterRequest("Alice", "a@b.com", "secret123")))
				.isInstanceOf(EmailAlreadyUsedException.class);
		verify(userRepository, never()).save(any());
	}

	@Test
	void loginReturnsATokenForValidCredentials() {
		User user = new User();
		user.setEmail("a@b.com");
		user.setPassword(passwordEncoder.encode("secret123"));
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
		when(jwtService.generateToken("a@b.com")).thenReturn("jwt");

		assertThat(authService.login(new LoginRequest("a@b.com", "secret123"))).isEqualTo("jwt");
	}

	@Test
	void loginRejectsAWrongPassword() {
		User user = new User();
		user.setEmail("a@b.com");
		user.setPassword(passwordEncoder.encode("secret123"));
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));

		assertThatThrownBy(() -> authService.login(new LoginRequest("a@b.com", "wrong")))
				.isInstanceOf(InvalidCredentialsException.class);
	}

	@Test
	void loginRejectsAnUnknownEmail() {
		when(userRepository.findByEmail("x@y.com")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.login(new LoginRequest("x@y.com", "secret123")))
				.isInstanceOf(InvalidCredentialsException.class);
	}
}
