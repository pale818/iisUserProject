# GUIDE

API KEY: reqres_4069f4ecc3d746579c1633cc62e1b89c
## Lecture References

These class presentations are available as reference material for this project:

- `../LECTURES/HTTP2.pdf`
- `../LECTURES/HTTP3.pdf`
- `../LECTURES/IIS_03_Services_2025-26.pdf`
- `../LECTURES/IIS_202526_02_Markup_languanges.pdf`
- `../LECTURES/IIS_202526_06_Web_Services.pdf`
- `../LECTURES/Introduction_to_Interoperability_-_2025-26-ENG.pdf`

## Current Project Setup

This project is currently a very minimal Spring Boot starter project.

### Basic project info

- Project type: Maven
- Java version: 17
- Spring Boot version: 3.5.13
- Group ID: `hr.algebra`
- Artifact ID: `iis_users`
- Project name: `iis_users_project`
- Base package: `hr.algebra.iisusers`

### Existing files and structure

- Main application class: `src/main/java/hr/algebra/iisusers/IisUsersProjectApplication.java`
- Default test class: `src/test/java/hr/algebra/iisusers/IisUsersProjectApplicationTests.java`
- Application config: `src/main/resources/application.properties`
- Maven wrapper scripts: `mvnw`, `mvnw.cmd`

### What is currently in the codebase

- A standard Spring Boot entrypoint class
- A default `contextLoads()` test
- An `application.properties` file with only:
  - `spring.application.name=iis_users_project`
- Empty resource folders for:
  - `src/main/resources/graphql`
  - `src/main/resources/static`
  - `src/main/resources/templates`

### Spring dependencies already included

The project already has these Spring starters and libraries in `pom.xml`:

- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-graphql`
- `spring-boot-starter-validation`
- `spring-boot-devtools`
- `postgresql`
- `lombok`
- `spring-boot-starter-test`
- `spring-graphql-test`
- `spring-security-test`

### Planned tools / plugins from the updated project plan

The updated high-level plan says the working environment should use these IDE/plugins/tools:

- Spring Boot
- Jakarta EE
- GraphQL
- Protocol Buffers
- gRPC
- Lombok
- Database Tools
- Maven
- SOAP UI plugin (optional)

Note: these are planning notes from the project document. They are not all configured in the project yet.

### Maven plugins / build setup

- `spring-boot-maven-plugin`
- `maven-compiler-plugin`
  - configured with Lombok annotation processing for compile and test-compile

### What this means technically

- The project uses the standard Spring MVC / servlet web stack
- JPA support is available, but no entities or repositories exist yet
- Security support is available, but no custom security or JWT configuration exists yet
- GraphQL support is available, but no schema or resolvers exist yet
- Validation support is available, but no DTO validation is implemented yet
- PostgreSQL support is currently included in `pom.xml`, but the updated project plan says the local project should use `H2` for the application database

### Updated architecture note from the plan

According to the updated `PROJECT_HIGH_PLAN` document, the intended final project shape is:

- public API source: `ReqRes Users`
- local custom API: your own Users CRUD API
- local database: `H2`
- user access through multiple technologies:
  - REST
  - SOAP
  - GraphQL
  - gRPC
- validation and security:
  - XSD validation for XML
  - JSON Schema validation for JSON
  - JWT access + refresh tokens
  - two roles: `read-only` and `full-access`
- application switch:
  - public ReqRes API
  - local custom API

### What is missing right now

This is still only a clean starter skeleton. The following parts are not built yet:

- database configuration
- user entity/model
- repository layer
- service layer
- REST controllers
- JWT authentication
- roles/authorization
- SOAP service
- XPath search
- XML/XSD validation
- JSON Schema validation
- GraphQL schema and resolvers
- gRPC service
- frontend/client UI

### Important update after rereading the project plan

The original starter project includes PostgreSQL support, but your updated project plan now says the custom API should save data in an `H2` database.

For this project, that is the simpler and better option because:

- it removes local PostgreSQL setup work
- it is easier to run and demo
- it fits the "do not overengineer" goal better
- it is enough for a student project unless your professor explicitly requires PostgreSQL

So from now on, the practical guide should assume:

- local database = `H2`
- the existing PostgreSQL dependency can be removed later when implementation starts

## Day 1 Plan

Goal: create the simplest possible backend foundation without overengineering.

### Day 1 tasks

1. Confirm the public API source for the project users domain.
   - Use the assigned public users endpoint.
   - If your assignment allows it, keep `ReqRes Users` as the public API source.

2. Define one simple internal `User` model for your own application.
   - Keep only the fields you really need.
   - Reuse this same model idea across REST, XML/JSON validation, SOAP, and GraphQL.

3. Configure the database connection.
   - Add H2 settings to `application.properties`.
   - Make sure the Spring Boot app can start with DB connectivity.

4. Create the basic backend structure.
   - `entity`
   - `repository`
   - `service`
   - `controller`
   - `config`
   - optional `dto` package if needed

5. Implement the first custom users backend slice.
   - Create the `User` entity
   - Create the JPA repository
   - Add a very simple service
   - Add one test endpoint or one initial `GET` endpoint

6. Verify the application starts successfully.
   - App starts
   - Spring context loads
   - Database connection works

### End of Day 1 target

By the end of Day 1, the project should have:

- a running Spring Boot backend
- a working H2 database connection
- a simple `User` entity stored in the database
- the first basic custom backend endpoint
- a clear package structure for the rest of the assignment

## Suggested day-by-day direction from the updated plan

### Day 1

- choose the final simple stack
- keep `ReqRes Users` as the public API source
- use `H2` as the local database
- create the `User` model/entity
- test fetching users from ReqRes
- create the first project structure for backend and client

### Day 2

- build custom `GET /users`
- build custom `POST /users`
- build custom `PUT /users/{id}`
- build custom `DELETE /users/{id}`
- save everything in H2

### Day 3

- create XSD for user XML
- create JSON Schema for user JSON
- build validation POST endpoint
- generate XML from ReqRes users
- add SOAP search
- search with XPath
- validate generated XML with Jakarta XML

### Day 4

- add login/auth
- add JWT access token + refresh token
- add `read-only` and `full-access` roles
- restrict methods by role
- build gRPC weather service with DHMZ XML

### Day 5

- finish the GUI client
- add switch between public API and custom API
- add GraphQL support
- test the whole flow
- prepare for demo

### Important note for this project

Keep the design simple. This is a student project, so the goal is not enterprise architecture. The goal is to satisfy the assignment requirements clearly, with code that is easy to understand and easy to demo.
