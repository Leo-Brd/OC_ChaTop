package com.openclassrooms.chatop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.chatop.dto.MessageRequest;
import com.openclassrooms.chatop.dto.MessageResponse;
import com.openclassrooms.chatop.exception.ForbiddenException;
import com.openclassrooms.chatop.exception.InvalidCredentialsException;
import com.openclassrooms.chatop.exception.ResourceNotFoundException;
import com.openclassrooms.chatop.model.Message;
import com.openclassrooms.chatop.model.Rental;
import com.openclassrooms.chatop.model.User;
import com.openclassrooms.chatop.repository.MessageRepository;
import com.openclassrooms.chatop.repository.RentalRepository;
import com.openclassrooms.chatop.repository.UserRepository;

@Service
public class MessageService {

	private final MessageRepository messageRepository;
	private final RentalRepository rentalRepository;
	private final UserRepository userRepository;

	public MessageService(MessageRepository messageRepository, RentalRepository rentalRepository,
			UserRepository userRepository) {
		this.messageRepository = messageRepository;
		this.rentalRepository = rentalRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public MessageResponse send(MessageRequest request, String authenticatedEmail) {
		User author = userRepository.findByEmail(authenticatedEmail)
				.orElseThrow(InvalidCredentialsException::new);

		// L'auteur est celui du token : on ne fait pas confiance au user_id du corps seul
		if (!author.getId().equals(request.userId())) {
			throw new ForbiddenException();
		}

		Rental rental = rentalRepository.findById(request.rentalId())
				.orElseThrow(() -> new ResourceNotFoundException("Rental"));

		Message message = new Message();
		message.setRental(rental);
		message.setUser(author);
		message.setMessage(request.message());
		messageRepository.save(message);

		return new MessageResponse("Message send with success");
	}
}
