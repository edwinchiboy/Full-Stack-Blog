
# Cutom Blog - Spring Boot Project

## Project Overview
This is a Spring Boot blog application using Gradle as the build tool. The project follows a standard layered architecture with controllers, services, repositories, DTOs, and models.

## Tech Stack
- **Framework**: Spring Boot
- **Build Tool**: Gradle
- **Package Structure**: `com.blog.cutom_blog`
- **Database**: [TBD - check application.properties]
- **Java Version**: [TBD - check build.gradle]

## Project Structure
```
src/main/java/com/blog/cutom_blog/
├── CutomBlogApplication.java    # Main application entry point
├── commons/                      # Common utilities and shared code
├── config/                       # Spring configuration classes
├── constants/                    # Application constants
├── controllers/                  # REST API endpoints
├── dtos/                         # Data Transfer Objects
├── exceptions/                   # Custom exceptions and error handling
├── models/                       # JPA entities/domain models
├── repositories/                 # Data access layer (JPA repositories)
├── services/                     # Business logic layer
└── utils/                        # Utility classes
```

## Current Work
- **Branch**: `feature/complete-the-registration-flow`
- **In Progress**: Working on registration service improvements

## Development Guidelines

### Code Style
- Follow standard Java naming conventions
- Use meaningful variable and method names
- Keep methods focused on single responsibility
- Add JavaDoc comments for public APIs

### Architecture Patterns
- **Controller Layer**: Handle HTTP requests/responses, validate input
- **Service Layer**: Implement business logic, coordinate between repositories
- **Repository Layer**: Data access and persistence
- **DTOs**: For data transfer between layers and API responses
- **Models**: JPA entities representing database tables

### Common Tasks

#### Running the Application
```bash
./gradlew bootRun
```

#### Running Tests
```bash
./gradlew test
```

#### Building the Project
```bash
./gradlew build
```

#### Clean Build
```bash
./gradlew clean build
```

## Key Features
- User registration flow
- [Add other features as they're implemented]

## Configuration
- Application properties: `src/main/resources/application.properties`

## Notes for Claude
- When adding new endpoints, create corresponding DTOs for request/response
- Always add proper exception handling using the custom exceptions
- Follow the existing package structure when adding new classes
- Consider adding service layer tests for new business logic
- Check application.properties for database and other configurations before making assumptions