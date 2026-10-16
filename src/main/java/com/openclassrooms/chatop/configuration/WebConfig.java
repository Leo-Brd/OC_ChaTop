package com.openclassrooms.chatop.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.openclassrooms.chatop.service.FileStorageService;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final FileStorageService fileStorageService;

	public WebConfig(FileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/**")
				.addResourceLocations(fileStorageService.getUploadDir().toUri().toString());
	}
}
