package com.openclassrooms.chatop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.openclassrooms.chatop.dto.RentalCreateRequest;
import com.openclassrooms.chatop.dto.RentalUpdateRequest;
import com.openclassrooms.chatop.exception.ForbiddenException;
import com.openclassrooms.chatop.exception.ResourceNotFoundException;
import com.openclassrooms.chatop.model.Rental;
import com.openclassrooms.chatop.model.User;
import com.openclassrooms.chatop.repository.RentalRepository;
import com.openclassrooms.chatop.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

	@Mock
	private RentalRepository rentalRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private FileStorageService fileStorageService;

	private RentalService rentalService;

	private User owner;

	@BeforeEach
	void setUp() {
		rentalService = new RentalService(rentalRepository, userRepository, fileStorageService);
		owner = new User();
		owner.setId(1);
		owner.setEmail("owner@b.com");
	}

	@Test
	void createStoresThePictureAndSetsTheAuthenticatedUserAsOwner() {
		MockMultipartFile picture = new MockMultipartFile("picture", "house.png", "image/png", new byte[] { 1 });
		when(userRepository.findByEmail("owner@b.com")).thenReturn(Optional.of(owner));
		when(fileStorageService.store(picture)).thenReturn("http://localhost:3001/images/x.png");

		var response = rentalService.create(
				new RentalCreateRequest("House", new BigDecimal("80"), new BigDecimal("600"), "Nice", picture),
				"owner@b.com");

		ArgumentCaptor<Rental> saved = ArgumentCaptor.forClass(Rental.class);
		verify(rentalRepository).save(saved.capture());
		assertThat(saved.getValue().getOwner()).isSameAs(owner);
		assertThat(saved.getValue().getPicture()).isEqualTo("http://localhost:3001/images/x.png");
		assertThat(saved.getValue().getName()).isEqualTo("House");
		assertThat(response.message()).isEqualTo("Rental created !");
	}

	@Test
	void updateChangesTheFieldsForTheOwner() {
		Rental rental = new Rental();
		rental.setOwner(owner);
		when(rentalRepository.findById(3)).thenReturn(Optional.of(rental));

		var response = rentalService.update(3,
				new RentalUpdateRequest("New", new BigDecimal("90"), new BigDecimal("700"), "Updated"), "owner@b.com");

		assertThat(rental.getName()).isEqualTo("New");
		assertThat(rental.getSurface()).isEqualByComparingTo("90");
		assertThat(rental.getDescription()).isEqualTo("Updated");
		assertThat(response.message()).isEqualTo("Rental updated !");
	}

	@Test
	void updateRejectsAUserWhoIsNotTheOwner() {
		Rental rental = new Rental();
		rental.setOwner(owner);
		rental.setName("Original");
		when(rentalRepository.findById(3)).thenReturn(Optional.of(rental));

		assertThatThrownBy(() -> rentalService.update(3,
				new RentalUpdateRequest("Hacked", new BigDecimal("1"), new BigDecimal("1"), "x"), "other@b.com"))
				.isInstanceOf(ForbiddenException.class);
		assertThat(rental.getName()).isEqualTo("Original");
	}

	@Test
	void updateRejectsAnUnknownRental() {
		when(rentalRepository.findById(99)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> rentalService.update(99,
				new RentalUpdateRequest("x", new BigDecimal("1"), new BigDecimal("1"), "x"), "owner@b.com"))
				.isInstanceOf(ResourceNotFoundException.class);
		verify(rentalRepository, never()).save(any());
	}
}
