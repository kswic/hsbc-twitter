# Twitter like demo app

### Basic info

It's a SpringBoot base application, so it can be easily built and run with Maven. 
Application use H2 in-memory database to store all required data. Spring Data JPA provides persistence.
Mapstruct is responsible for entity-DTO mappings. API has been documented with Swagger UI (URL below).
Some integration tests have been prepared for selected use cases.

Swagger UI URL: http://localhost:8080/swagger-ui
H2 console URL: http://localhost:8080/h2-console

DB URL and authorization credentials can be found in application.properties file.

To run the app execute command:
>mvnw spring-boot:run