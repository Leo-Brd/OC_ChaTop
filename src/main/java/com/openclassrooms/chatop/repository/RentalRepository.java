package com.openclassrooms.chatop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openclassrooms.chatop.model.Rental;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
}
