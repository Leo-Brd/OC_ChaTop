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

import com.openclassrooms.chatop.dto.MessageRequest;
import com.openclassrooms.chatop.exception.ForbiddenException;
import com.openclassrooms.chatop.exception.ResourceNotFoundException;
import com.openclassrooms.chatop.model.Message;
import com.openclassrooms.chatop.model.Rental;
import com.openclassrooms.chatop.model.User;
import com.openclassrooms.chatop.repository.MessageRepository;
import com.openclassrooms.chatop.repository.RentalRepository;
import com.openclassrooms.chatop.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private RentalRepository rentalRepository;

	@Mock
	private UserRepository userRepository;

	private MessageService messageService;

	private User author;

	@BeforeEach
	void setUp() {
		messageService = new MessageService(messageRepository, rentalRepository, userRepository);
		author = new User();
		author.setId(1);
		author.setEmail("a@b.com");
	}

	@Test
	void sendSavesTheMessageForTheAuthenticatedUser() {
		Rental rental = new Rental();
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(author));
		when(rentalRepository.findById(5)).thenReturn(Optional.of(rental));

		var response = messageService.send(new MessageRequest(5, 1, "Bonjour"), "a@b.com");

		ArgumentCaptor<Message> saved = ArgumentCaptor.forClass(Message.class);
		verify(messageRepository).save(saved.capture());
		assertThat(saved.getValue().getUser()).isSameAs(author);
		assertThat(saved.getValue().getRental()).isSameAs(rental);
		assertThat(saved.getValue().getMessage()).isEqualTo("Bonjour");
		assertThat(response.message()).isEqualTo("Message send with success");
	}

	@Test
	void sendRejectsAUserIdThatIsNotTheAuthenticatedUser() {
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(author));

		assertThatThrownBy(() -> messageService.send(new MessageRequest(5, 2, "Bonjour"), "a@b.com"))
				.isInstanceOf(ForbiddenException.class);
		verify(messageRepository, never()).save(any());
	}

	@Test
	void sendRejectsAnUnknownRental() {
		when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(author));
		when(rentalRepository.findById(99)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> messageService.send(new MessageRequest(99, 1, "Bonjour"), "a@b.com"))
				.isInstanceOf(ResourceNotFoundException.class);
		verify(messageRepository, never()).save(any());
	}
}
