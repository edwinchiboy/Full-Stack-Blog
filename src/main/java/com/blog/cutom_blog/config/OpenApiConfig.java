package com.blog.cutom_blog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Crypto Blog API",
        version = "1.0.0",
        description = """
            # Crypto Blog REST API Documentation

            A comprehensive blog platform for cryptocurrency news and updates.

            ## Features
            - 🔐 User Authentication & Authorization (JWT)
            - 📝 Blog Post Management (CRUD)
            - 💬 Comment System
            - 📧 Email Notifications & Subscriptions
            - 📊 Admin Dashboard with Statistics
            - 🔒 Password Reset Functionality
            - 🏷️ Category Management

            ## Authentication
            Most endpoints require JWT authentication. Use the `/api/auth/signin` endpoint to obtain a token.

            ## Roles
            - **READER**: Can view posts, comment, and subscribe
            - **ADMIN**: Full access to all features
            """,
        contact = @Contact(
            name = "Crypto Blog Team",
            email = "support@cryptoblog.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "Development Server"
        ),
        @Server(
            url = "https://api.cryptoblog.com",
            description = "Production Server"
        )
    },
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT Bearer Token Authentication",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}