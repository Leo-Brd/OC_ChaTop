package com.openclassrooms.chatop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.chatop.dto.RentalResponse;
import com.openclassrooms.chatop.dto.RentalsResponse;
import com.openclassrooms.chatop.exception.ResourceNotFoundException;
import com.openclassrooms.chatop.repository.RentalRepository;

@Service
public class RentalService {

	private final RentalRepository rentalRepository;

	public RentalService(RentalRepository rentalRepository) {
		this.rentalRepository = rentalRepository;
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
}
