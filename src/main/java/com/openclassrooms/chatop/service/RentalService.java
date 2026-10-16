package com.openclassrooms.chatop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.chatop.dto.MessageResponse;
import com.openclassrooms.chatop.dto.RentalCreateRequest;
import com.openclassrooms.chatop.dto.RentalResponse;
import com.openclassrooms.chatop.dto.RentalUpdateRequest;
import com.openclassrooms.chatop.dto.RentalsResponse;
import com.openclassrooms.chatop.exception.ForbiddenException;
import com.openclassrooms.chatop.exception.InvalidCredentialsException;
import com.openclassrooms.chatop.exception.ResourceNotFoundException;
import com.openclassrooms.chatop.model.Rental;
import com.openclassrooms.chatop.model.User;
import com.openclassrooms.chatop.repository.RentalRepository;
import com.openclassrooms.chatop.repository.UserRepository;

@Service
public class RentalService {

	private final RentalRepository rentalRepository;
	private final UserRepository userRepository;
	private final FileStorageService fileStorageService;

	public RentalService(RentalRepository rentalRepository, UserRepository userRepository,
			FileStorageService fileStorageService) {
		this.rentalRepository = rentalRepository;
		this.userRepository = userRepository;
		this.fileStorageService = fileStorageService;
	}

	@Transactional(readOnly = true)
	public RentalsResponse findAll() {
		List<RentalResponse> rentals = rentalRepository.findAll().stream()
				.map(RentalResponse::from)
				.toList();
		return new RentalsResponse(rentals);
	}

	@Transactional(readOnly = true)
	public RentalResponse findById(Integer id) {
		return rentalRepository.findById(id)
				.map(RentalResponse::from)
				.orElseThrow(() -> new ResourceNotFoundException("Rental"));
	}

	@Transactional
	public MessageResponse create(RentalCreateRequest request, String ownerEmail) {
		User owner = userRepository.findByEmail(ownerEmail)
				.orElseThrow(InvalidCredentialsException::new);

		Rental rental = new Rental();
		rental.setName(request.name());
		rental.setSurface(request.surface());
		rental.setPrice(request.price());
		rental.setDescription(request.description());
		rental.setPicture(fileStorageService.store(request.picture()));
		rental.setOwner(owner);
		rentalRepository.save(rental);

		return new MessageResponse("Rental created !");
	}

	@Transactional
	public MessageResponse update(Integer id, RentalUpdateRequest request, String ownerEmail) {
		Rental rental = rentalRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Rental"));

		if (!rental.getOwner().getEmail().equals(ownerEmail)) {
			throw new ForbiddenException();
		}

		rental.setName(request.name());
		rental.setSurface(request.surface());
		rental.setPrice(request.price());
		rental.setDescription(request.description());

		return new MessageResponse("Rental updated !");
	}
}
