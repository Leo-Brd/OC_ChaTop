package com.openclassrooms.chatop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import com.openclassrooms.chatop.exception.InvalidFileException;

class FileStorageServiceTest {

	@TempDir
	Path tempDir;

	private FileStorageService storageService;

	@BeforeEach
	void setUp() {
		storageService = new FileStorageService(tempDir.toString(), "http://localhost:3001");
	}

	@Test
	void storeSavesTheFileUnderARandomNameAndReturnsItsUrl() throws Exception {
		MockMultipartFile file = new MockMultipartFile("picture", "../../house.JPG", "image/jpeg", new byte[] { 1, 2, 3 });

		String url = storageService.store(file);

		assertThat(url).startsWith("http://localhost:3001/images/").endsWith(".jpg").doesNotContain("house");
		String filename = url.substring(url.lastIndexOf('/') + 1);
		assertThat(Files.readAllBytes(tempDir.resolve(filename))).containsExactly(1, 2, 3);
	}

	@Test
	void storeRejectsANonImageExtension() {
		MockMultipartFile file = new MockMultipartFile("picture", "script.html", "image/jpeg", new byte[] { 1 });

		assertThatThrownBy(() -> storageService.store(file)).isInstanceOf(InvalidFileException.class);
	}

	@Test
	void storeRejectsANonImageContentType() {
		MockMultipartFile file = new MockMultipartFile("picture", "house.png", "text/html", new byte[] { 1 });

		assertThatThrownBy(() -> storageService.store(file)).isInstanceOf(InvalidFileException.class);
	}

	@Test
	void storeRejectsAnEmptyFile() {
		MockMultipartFile file = new MockMultipartFile("picture", "house.png", "image/png", new byte[0]);

		assertThatThrownBy(() -> storageService.store(file)).isInstanceOf(InvalidFileException.class);
	}
}
