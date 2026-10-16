package com.openclassrooms.chatop.configuration;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(info = @Info(
		title = "ChaTop API",
		version = "1.0.0",
		description = """
				REST API of the ChaTop seasonal rental portal.

				To call protected routes: create an account with POST /api/auth/register \
				(or log in with POST /api/auth/login), copy the returned token, click "Authorize" \
				and paste it (without the "Bearer" prefix)."""))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
