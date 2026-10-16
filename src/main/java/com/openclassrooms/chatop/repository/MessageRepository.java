package com.openclassrooms.chatop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openclassrooms.chatop.model.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
