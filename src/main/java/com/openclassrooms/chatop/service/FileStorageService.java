package com.openclassrooms.chatop.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.openclassrooms.chatop.exception.InvalidFileException;

@Service
public class FileStorageService {

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

	private final Path uploadDir;
	private final String baseUrl;

	public FileStorageService(@Value("${app.upload-dir}") String uploadDir, @Value("${app.base-url}") String baseUrl) {
		this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
		this.baseUrl = baseUrl;
	}

	/**
	 * Enregistre l'image sous un nom aléatoire et renvoie son URL publique.
	 */
	public String store(MultipartFile file) {
		String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
		String contentType = file.getContentType();

		if (file.isEmpty()
				|| extension == null
				|| !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())
				|| contentType == null
				|| !contentType.startsWith("image/")) {
			throw new InvalidFileException();
		}

		String filename = UUID.randomUUID() + "." + extension.toLowerCase();
		try (InputStream content = file.getInputStream()) {
			Files.createDirectories(uploadDir);
			Files.copy(content, uploadDir.resolve(filename));
		} catch (IOException e) {
			throw new UncheckedIOException("Unable to store the file", e);
		}

		return baseUrl + "/images/" + filename;
	}

	public Path getUploadDir() {
		return uploadDir;
	}
}
