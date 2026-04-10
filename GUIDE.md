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



### Day 2 tasks

1. Review the Day 1 endpoints and clean up what is only temporary.
    - Keep the local users endpoints.
    - Keep the public ReqRes fetch endpoint because it is still useful.
    - Keep the import endpoint only if it helps with testing/demo.

2. Complete local REST CRUD for users.
    - `GET /api/users`
    - `GET /api/users/{id}`
    - `POST /api/users`
    - `PUT /api/users/{id}`
    - `DELETE /api/users/{id}`

3. Add simple update logic in the service layer.
    - Find user by id.
    - If user exists, update fields.
    - If user does not exist, return a simple not found error.

4. Add simple delete logic in the service layer.
    - Find by id before deleting.
    - Return a clear response if the user does not exist.

5. Add a basic DTO decision if needed.
    - If possible, stay simple and use the entity directly for Day 2.
    - Only introduce request/response DTOs if the controller becomes messy.

6. Add minimal error handling.
    - handle user not found
    - return HTTP status codes clearly
    - keep messages simple and readable

7. Test all CRUD operations manually.
    - create user
    - read all users
    - read one user by id
    - update user
    - delete user
    - verify results in H2 console

### End of Day 2 target

By the end of Day 2, the project should have:

- a complete local Users CRUD REST API
- all four required HTTP methods working
- data saved in H2
- simple not found handling
- endpoints that are easy to test and easy to demo

### Day 2 manual test commands

Get all users:

```bash
curl -s http://localhost:8080/api/users
```

Get one user by id:

```bash
curl -s http://localhost:8080/api/users/1
```

Create a new user:

```bash
curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email":"new.user@example.com","firstName":"New","lastName":"User","avatar":"https://example.com/avatar.jpg"}'
```

Update a user:

```bash
curl -s -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"email":"updated.user@example.com","firstName":"Updated","lastName":"User","avatar":"https://example.com/updated-avatar.jpg"}'
```

Delete a user:

```bash
curl -s -X DELETE http://localhost:8080/api/users/1
```

Check not found:

```bash
curl -s http://localhost:8080/api/users/999
```

## Day 3 Plan

Goal: satisfy three assignment requirements — XML/JSON validation with save, SOAP search with XPath, and Jakarta XML validation of generated XML.

### Requirements from the assignment (what must be done)

**Requirement 1 — XML and JSON validation + save (2+2+2 points)**
- one POST endpoint that accepts an XML body
- validate the XML against an XSD schema
- if valid → save the user to the H2 database
- if invalid → return the validation errors to the user
- one POST endpoint that accepts a JSON body
- validate the JSON against a JSON Schema file
- if valid → save the user to the H2 database
- if invalid → return the validation errors to the user

**Requirement 2 — SOAP search with XPath (4+2+4 points)**
- on the backend, generate an XML file containing users fetched from the ReqRes REST API
- expose a SOAP endpoint that accepts a search term
- inside the SOAP service, use XPath on that generated XML to filter users that match the term
- return the matching users as the SOAP response

**Requirement 3 — Jakarta XML validation of generated XML (4+2+2 points)**
- using Jakarta XML Bind (JAXB), validate the generated XML from Requirement 2 against the XSD
- return validation messages if the XML is not valid
- expose this as a REST endpoint so it is easy to call and demo

### New packages / files to create

```
src/main/resources/
  user.xsd                          ← XSD schema (shared by all XML operations)
  user-schema.json                  ← JSON Schema for user JSON
  users-soap.xsd                    ← XSD for SOAP request/response types (auto-generates WSDL)

src/main/java/hr/algebra/iisusers/
  xml/
    UserJaxb.java                   ← JAXB-annotated class for a single <user>
    UsersJaxb.java                  ← JAXB-annotated class for <users> collection
    XmlGenerationService.java       ← fetches ReqRes users, builds XML string, stores it
    XmlValidationService.java       ← ONE service: uses JAXB + user.xsd for all XML validation
    XmlController.java              ← REST: generate, validate-save, jakarta-validate

  json/
    JsonValidationService.java      ← validates JSON string against user-schema.json
    JsonController.java             ← REST: validate-save

  soap/
    SearchUsersRequest.java         ← JAXB request POJO
    SearchUsersResponse.java        ← JAXB response POJO
    UserSoapService.java            ← @Endpoint, uses XPath on generated XML
    WebServiceConfig.java           ← registers Spring WS servlet, exposes WSDL
```

Keep each class small and focused. No extra layers.

### Why one XmlValidationService is enough

JAXB (Jakarta XML Bind) covers both assignment requirements:
- Requirement 1 uses JAXB to validate a user-submitted `<user>` before saving
- Requirement 3 uses the same JAXB service to validate the generated `<users>` XML
One service, two methods (`validateUser` and `validateUsers`), same XSD.

### Day 3 tasks

1. **Create `user.xsd`** in `src/main/resources/`
   - one root element `<user>` with child elements: `id`, `email`, `firstName`, `lastName`, `avatar`
   - all fields required
   - `id` is `xs:integer`, all others are `xs:string`
   - this one file is the contract for all XML work in the project

2. **Create `user-schema.json`** in `src/main/resources/`
   - describe a user object with the same five fields
   - all fields required
   - `id` is type `integer`, all others are type `string`

3. **Add two dependencies to `pom.xml`**
   - `everit-json-schema` (or `networknt/json-schema-validator`) for JSON Schema validation
   - `spring-ws-core` for the SOAP endpoint
   - nothing else needed; JAXB is already bundled with Java 17 via Jakarta EE

4. **Create `XmlGenerationService`**
   - call `userService.getPublicUsers(page)` to get ReqRes users
   - build a `<users>` XML document with one `<user>` child per result using `DocumentBuilder`
   - use `Transformer` to serialize it to a `String`
   - store the last generated XML as a field so the SOAP service and Jakarta XML validator can reuse it
   - this is the "prepared XML file" the assignment refers to

5. **Create `XmlValidationService`**
   - load `user.xsd` from classpath using `SchemaFactory`
   - expose `validate(String xml)` → returns a list of error message strings (empty list = valid)
   - uses standard `javax.xml.validation` — no extra library needed

6. **Create `JakartaXmlValidationService`**
   - this satisfies Requirement 3 explicitly
   - create a simple JAXB-annotated class `UserJaxb` that mirrors the XSD structure
   - use `JAXBContext` + `Unmarshaller` with a `Schema` attached to validate during unmarshal
   - collect `ValidationEvent` messages and return them
   - expose `validate(String xml)` → returns a list of error strings

7. **Create `XmlController`**
   - `GET /api/xml/generate?page=1` → calls `XmlGenerationService`, returns XML as `text/xml`
   - `POST /api/xml/validate-save` → receives XML body, validates with `XmlValidationService`, saves user to DB if valid, returns errors if not
   - `POST /api/xml/jakarta-validate` → receives XML body, validates with `JakartaXmlValidationService`, returns ok or list of errors

8. **Create `JsonValidationService`**
   - load `user-schema.json` from classpath
   - expose `validate(String json)` → returns a list of error strings
   - use the `everit` or `networknt` library

9. **Create `JsonController`**
   - `POST /api/json/validate-save` → receives JSON body as plain text, validates with `JsonValidationService`, if valid parse it and save to DB, return errors if not

10. **Create `UserXmlRequest`**
    - a plain Java class with one field: `String searchTerm`
    - annotated with `@XmlRootElement` so Spring WS can unmarshal it

11. **Create `UserSoapService`**
    - annotated with `@Endpoint`
    - calls `XmlGenerationService` to get the latest generated XML (or generates fresh on demand)
    - builds an `XPath` expression like `//user[contains(firstName, 'term') or contains(lastName, 'term')]`
    - evaluates it on the XML document and collects matching nodes
    - returns the matching users as an XML string in the response

12. **Create `WebServiceConfig`**
    - register `MessageDispatcherServlet` on `/ws/*`
    - expose WSDL at `/ws/users.wsdl`
    - minimal config, just what Spring WS requires

13. **Verify everything works**
    - app starts with no errors
    - `GET /api/xml/generate` returns XML with ReqRes users
    - `POST /api/xml/validate-save` with valid XML saves user and returns it
    - `POST /api/xml/validate-save` with invalid XML returns error list
    - `POST /api/xml/jakarta-validate` validates and returns messages
    - `POST /api/json/validate-save` with valid JSON saves user and returns it
    - `POST /api/json/validate-save` with invalid JSON returns error list
    - WSDL visible at `http://localhost:8080/ws/users.wsdl`
    - SOAP search with a term returns matching users

### End of Day 3 target

By the end of Day 3, the project should have:

- `user.xsd` and `user-schema.json` as the validation contracts
- REST endpoints that validate XML/JSON and save to DB only if valid
- a backend XML generation service using ReqRes data
- a SOAP search endpoint that uses XPath on the generated XML
- a Jakarta XML (JAXB) validation endpoint for the generated XML
- all three assignment requirements for Day 3 fully covered

### Day 3 manual test commands

Generate XML from ReqRes (call this first — SOAP and Jakarta validate use this data):

```bash
curl -s "http://localhost:8080/api/xml/generate?page=1"
```

Validate and save a user via XML (valid):

```bash
curl -s -X POST http://localhost:8080/api/xml/validate-save \
  -H "Content-Type: text/plain" \
  -d '<user><id>1</id><email>test@example.com</email><firstName>Test</firstName><lastName>User</lastName><avatar>https://example.com/avatar.jpg</avatar></user>'
```

Validate XML — invalid (missing fields, should return errors):

```bash
curl -s -X POST http://localhost:8080/api/xml/validate-save \
  -H "Content-Type: text/plain" \
  -d '<user><id>1</id><email>test@example.com</email></user>'
```

Jakarta XML validation of the generated XML (call generate first, then this):

```bash
curl -s http://localhost:8080/api/xml/jakarta-validate
```

Validate and save a user via JSON (valid):

```bash
curl -s -X POST http://localhost:8080/api/json/validate-save \
  -H "Content-Type: text/plain" \
  -d '{"id":1,"email":"test@example.com","firstName":"Test","lastName":"User","avatar":"https://example.com/avatar.jpg"}'
```

Validate JSON — invalid (missing fields, should return errors):

```bash
curl -s -X POST http://localhost:8080/api/json/validate-save \
  -H "Content-Type: text/plain" \
  -d '{"id":1,"email":"test@example.com"}'
```

Check WSDL is available:

```bash
curl -s http://localhost:8080/ws/users.wsdl
```

---


## Day 4 Plan

Goal: add JWT authentication with two roles, protect all existing endpoints by role, and build a gRPC service that fetches live weather data from DHMZ XML.

### What must be done (two separate parts)

**Part 1 — JWT authentication + roles (assignment requirement)**
- a login endpoint that returns a short-lived access token and a long-lived refresh token
- a refresh endpoint that accepts the refresh token and returns a new access token
- two roles: `READ_ONLY` (GET requests only) and `FULL_ACCESS` (all requests)
- all existing API endpoints must require a valid JWT — no more open access

**Part 2 — gRPC weather service with DHMZ XML (assignment requirement)**
- fetch the live DHMZ Croatian weather XML from their public endpoint
- parse selected weather station data from the XML
- expose the data through a gRPC service with a simple request/response shape
- a plain REST wrapper endpoint so the gRPC service is also easy to demo without a gRPC client

### New packages / files to create

```
src/main/java/hr/algebra/iisusers/
  auth/
    AppUser.java              ← JPA entity: id, username, password (bcrypt), role, refreshToken
    AppUserRepository.java    ← JPA repository for AppUser
    AppUserDetailsService.java ← implements UserDetailsService for Spring Security
    JwtService.java           ← generates and validates JWT access + refresh tokens
    JwtFilter.java            ← OncePerRequestFilter: reads token from header, sets SecurityContext
    AuthController.java       ← POST /auth/login, POST /auth/refresh
    AuthRequest.java          ← DTO: username, password
    AuthResponse.java         ← DTO: accessToken, refreshToken

  grpc/
    WeatherGrpcService.java   ← @GrpcService implementation of WeatherServiceGrpc
    WeatherRestController.java ← GET /api/weather — calls gRPC service and returns JSON

src/main/proto/
  weather.proto               ← defines WeatherService, WeatherRequest, WeatherResponse
```

Update `config/SecurityConfig.java` — replace the current "permit all" config with JWT filter chain and role-based rules.

### Step-by-step tasks

#### 1. Add dependencies to `pom.xml`

Add these three dependencies:

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>

<!-- gRPC -->
<dependency>
    <groupId>net.devh</groupId>
    <artifactId>grpc-server-spring-boot-starter</artifactId>
    <version>3.1.0.RELEASE</version>
</dependency>
```

Add the protobuf Maven plugin inside `<build><plugins>`:

```xml
<plugin>
    <groupId>com.github.os72</groupId>
    <artifactId>protoc-jar-maven-plugin</artifactId>
    <version>3.11.4</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals><goal>run</goal></goals>
            <configuration>
                <protocVersion>3.21.7</protocVersion>
                <inputDirectories>
                    <include>src/main/proto</include>
                </inputDirectories>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Add to `application.properties`:
```properties
jwt.secret=replace-with-a-long-random-base64-secret-at-least-256-bits
jwt.access-token-expiry-ms=900000
jwt.refresh-token-expiry-ms=604800000
grpc.server.port=9090
```

#### 2. Create `AppUser` entity

- fields: `id` (Long, generated), `username` (String, unique), `password` (String, bcrypt hash), `role` (String — either `"READ_ONLY"` or `"FULL_ACCESS"`), `refreshToken` (String, nullable)
- annotate with `@Entity`, `@Table(name = "app_users")`
- use Lombok `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`

#### 3. Create `AppUserRepository`

- extends `JpaRepository<AppUser, Long>`
- add `Optional<AppUser> findByUsername(String username)`
- add `Optional<AppUser> findByRefreshToken(String refreshToken)`

#### 4. Create `AppUserDetailsService`

- implements `UserDetailsService`
- load user by username from `AppUserRepository`
- convert role string to `GrantedAuthority` (`ROLE_READ_ONLY` or `ROLE_FULL_ACCESS`)
- annotate with `@Service`

#### 5. Create `JwtService`

- inject the secret and expiry values from `application.properties` using `@Value`
- `generateAccessToken(String username)` → builds a JWT signed with HMAC-SHA256, sets subject = username, expiry = access token expiry
- `generateRefreshToken(String username)` → same but longer expiry
- `extractUsername(String token)` → parses the token and returns the subject
- `isTokenValid(String token)` → returns true if the token parses correctly and is not expired
- use `io.jsonwebtoken.Jwts` from the `jjwt-api` library

#### 6. Create `JwtFilter`

- extends `OncePerRequestFilter`
- reads `Authorization: Bearer <token>` header
- if the header is present and the token is valid, load the user via `AppUserDetailsService`, build a `UsernamePasswordAuthenticationToken`, and set it on `SecurityContextHolder`
- if the header is missing or invalid, do nothing — the security chain will reject the request

#### 7. Create `AuthController`

- `POST /auth/login`
    - accepts `AuthRequest` (username + password)
    - use `AuthenticationManager` to authenticate credentials
    - if valid: generate access token + refresh token, store refresh token on `AppUser`, return `AuthResponse`
    - if invalid: return `401 Unauthorized`
- `POST /auth/refresh`
    - accepts a plain string body: the refresh token
    - look up the `AppUser` by refresh token using the repository
    - if found and token not expired: generate a new access token and return it
    - if not found or expired: return `401 Unauthorized`

#### 8. Update `SecurityConfig`

Replace the current "permit all" config with this role-based setup:

- permit `/auth/**`, `/h2-console/**`, `/ws/**` without a token (public)
- for `/api/**`:
    - GET requests → require `ROLE_READ_ONLY` or `ROLE_FULL_ACCESS`
    - POST, PUT, DELETE requests → require `ROLE_FULL_ACCESS` only
- add `JwtFilter` before `UsernamePasswordAuthenticationFilter`
- keep `csrf` disabled, keep `frameOptions` for H2 console
- register `AuthenticationManager` as a `@Bean` so `AuthController` can inject it
- register `PasswordEncoder` (BCrypt) as a `@Bean`

#### 9. Create two test users at startup

In the main application class or a `@Component` with `CommandLineRunner`:
- check if users already exist (avoid duplicates on restart)
- create user `reader` with password `reader123` and role `READ_ONLY`
- create user `admin` with password `admin123` and role `FULL_ACCESS`
- encode passwords with the BCrypt bean before saving

#### 10. Create `weather.proto`

Path: `src/main/proto/weather.proto`

```proto
syntax = "proto3";
option java_package = "hr.algebra.iisusers.grpc";
option java_outer_classname = "WeatherProto";

service WeatherService {
    rpc GetWeather (WeatherRequest) returns (WeatherResponse);
}

message WeatherRequest {
    string station = 1;
}

message WeatherResponse {
    string station = 1;
    string temperature = 2;
    string description = 3;
    string timestamp = 4;
}
```

#### 11. Create `WeatherGrpcService`

- annotate with `@GrpcService`
- extends the generated `WeatherServiceGrpc.WeatherServiceImplBase`
- in `getWeather(WeatherRequest request, StreamObserver<WeatherResponse> responseObserver)`:
    - fetch the DHMZ XML from `http://vrijeme.hr/hrvatska_n.xml` using `RestTemplate`
    - parse the XML with `DocumentBuilder`
    - find the `<Grad>` node whose `<GradIme>` matches the requested station (case-insensitive), or default to the first station if none found
    - extract `<Temp>`, `<Vrijeme>`, and `<Datum>` + `<Sat>` fields
    - build and return a `WeatherResponse`

#### 12. Create `WeatherRestController`

- `GET /api/weather?station=Zagreb` → calls the gRPC service using a generated blocking stub, returns the response as JSON
- this is purely for demo convenience — no gRPC client setup needed for the defense
- annotate with `@RestController`

### End of Day 4 target

By the end of Day 4, the project should have:

- a login endpoint that returns JWT access + refresh tokens
- a refresh endpoint that issues a new access token
- all `/api/**` endpoints protected by JWT, with `READ_ONLY` users restricted to GET only
- two pre-seeded test users (`reader` / `admin`) ready to use for demo
- a gRPC `WeatherService` running on port 9090 that fetches and returns live DHMZ weather data
- a REST wrapper at `/api/weather` for easy demo without a gRPC client

### Day 4 manual test commands

Register a token as `reader` (READ_ONLY):

```bash
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"reader","password":"reader123"}'
```

Register a token as `admin` (FULL_ACCESS):

```bash
curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Use the access token to call a protected endpoint (replace TOKEN):

```bash
curl -s http://localhost:8080/api/users \
  -H "Authorization: Bearer TOKEN"
```

Try to create a user as `reader` — should return 403:

```bash
curl -s -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer READER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"email":"x@x.com","firstName":"X","lastName":"Y","avatar":"https://example.com/a.jpg"}'
```

Refresh the access token:

```bash
curl -s -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: text/plain" \
  -d 'REFRESH_TOKEN'
```

Get weather via REST wrapper (no gRPC client needed):

```bash
curl -s "http://localhost:8080/api/weather?station=Zagreb" -H "Authorization: Bearer ACCESS_TOKEN"
```

Note: paste everything on one line, no backslash. Replace ACCESS_TOKEN with the accessToken from the login response.

## Day 5 Plan

Goal: add GraphQL support, build a simple GUI client with an API switch, and verify the full project end-to-end.

### What must be done (two separate parts)

**Part 1 — GraphQL (assignment requirement)**
- expose the local users through a GraphQL API
- support at least one query (get all users or get one user)
- support at least one mutation (create or update a user)
- protect it with JWT the same way as the REST endpoints

**Part 2 — GUI client with API switch**
- a simple HTML page served by the same Spring Boot app (no separate frontend project needed)
- a login form that stores the JWT access token
- a users list view that can toggle between the public ReqRes API and the local custom API
- simple buttons to demo create/delete (FULL_ACCESS only)

### New packages / files to create

```
src/main/resources/graphql/
  schema.graphqls                  ← GraphQL schema (types, queries, mutations)

src/main/java/hr/algebra/iisusers/
  graphql/
    UserGraphQLController.java     ← @QueryMapping and @MutationMapping methods

src/main/resources/static/
  index.html                       ← single-page GUI client
```

### Step-by-step tasks

#### 1. Create the GraphQL schema

Path: `src/main/resources/graphql/schema.graphqls`

```graphql
type User {
    id: ID
    email: String
    firstName: String
    lastName: String
    avatar: String
}

type Query {
    users: [User]
    user(id: ID!): User
}

input UserInput {
    email: String!
    firstName: String!
    lastName: String!
    avatar: String
}

type Mutation {
    createUser(input: UserInput!): User
    deleteUser(id: ID!): String
}
```

#### 2. Create `UserGraphQLController`

- annotate with `@Controller` (not `@RestController` — Spring GraphQL requires plain `@Controller`)
- inject `UserService`
- `@QueryMapping users` → calls `userService.getAllLocalUsers()`
- `@QueryMapping user` → calls `userService.getLocalUserById(id)`
- `@MutationMapping createUser` → maps the `UserInput` to a `User` entity and calls `userService.saveLocalUser()`
- `@MutationMapping deleteUser` → calls `userService.deleteLocalUser(id)`, returns a confirmation string

#### 3. Protect the GraphQL endpoint

Spring Boot auto-configures GraphQL at `/graphql`. Add it to `SecurityConfig` so it requires a valid JWT (same as `/api/**`):

In `SecurityConfig`, add `/graphql` to the authenticated block:
```java
.requestMatchers(HttpMethod.GET, "/api/**", "/graphql").hasAnyRole("READ_ONLY", "FULL_ACCESS")
.requestMatchers(HttpMethod.POST, "/api/**", "/graphql").hasAnyRole("READ_ONLY", "FULL_ACCESS")
```

Note: GraphQL always uses POST. Both roles can query; you can optionally add mutation protection inside the resolver itself by checking the authentication principal if needed.

#### 4. Create the GUI client

Path: `src/main/resources/static/index.html`

Keep it simple — plain HTML + vanilla JavaScript, no frameworks. Structure:

**Login section**
- username + password fields
- a Login button that calls `POST /auth/login` and stores the `accessToken` in a JS variable

**Users section (shown after login)**
- a toggle/switch: `Public API (ReqRes)` vs `Local API (H2)`
  - Public: calls `GET /api/users/public`
  - Local: calls `GET /api/users`
- a table that displays the returned users
- a simple Create User form (only enabled/shown for FULL_ACCESS — check the JWT role or just show it and let the backend return 403)
- a Delete button per row that calls `DELETE /api/users/{id}`

**Weather section**
- a text input for station name
- a button that calls `GET /api/weather?station=...` and shows the result

All fetch calls must include `Authorization: Bearer <token>` in the headers.

#### 5. Verify GraphQL works

Spring Boot includes a GraphiQL browser UI at `/graphiql` by default (enable it in `application.properties`):

```properties
spring.graphql.graphiql.enabled=true
```

Test in the browser at `http://localhost:8080/graphiql` (you will need to add the auth header — use a browser extension like ModHeader, or test with curl).

Test query with curl (single line, replace TOKEN):

```bash
curl -s -X POST http://localhost:8080/graphql -H "Authorization: Bearer TOKEN" -H "Content-Type: application/json" -d '{"query":"{ users { id email firstName lastName } }"}'
```

Test mutation with curl (single line, replace TOKEN):

```bash
curl -s -X POST http://localhost:8080/graphql -H "Authorization: Bearer TOKEN" -H "Content-Type: application/json" -d '{"query":"mutation { createUser(input: { email: \"gql@test.com\", firstName: \"GQL\", lastName: \"Test\", avatar: \"\" }) { id email } }"}'
```

#### 6. Full end-to-end test checklist

Go through each assignment requirement once before the demo:

- [ ] Login as `reader` → get token → call `GET /api/users` → see users
- [ ] Login as `reader` → try `POST /api/users` → get 403
- [ ] Login as `admin` → call `POST /api/users` → user created
- [ ] Call `POST /api/xml/validate-save` with valid XML → user saved
- [ ] Call `POST /api/xml/validate-save` with invalid XML → errors returned
- [ ] Call `POST /api/json/validate-save` with valid JSON → user saved
- [ ] Call `POST /api/json/validate-save` with invalid JSON → errors returned
- [ ] Call `GET /api/xml/generate` → XML with ReqRes users returned
- [ ] Call SOAP endpoint → XPath search returns matching users
- [ ] Call `GET /api/weather?station=Zagreb` with token → live weather returned
- [ ] GraphQL `users` query → list returned
- [ ] GraphQL `createUser` mutation → user created
- [ ] GUI login form works
- [ ] GUI API switch toggles between ReqRes and local users
- [ ] Token refresh works

### End of Day 5 target

By the end of Day 5, the project should have:

- a GraphQL API for users (query + mutation) protected by JWT
- a simple browser GUI served from the same app at `http://localhost:8080`
- a working API toggle in the GUI between public ReqRes and local H2
- all assignment requirements verified end-to-end and ready to demo

---

## Suggested day-by-day direction from the updated plan

### Day 1

- choose the final simple stack
- keep `ReqRes Users` as the public API source
- use `H2` as the local database
- create the `User` model/entity
- test fetching users from ReqRes
- create the first project structure for backend and client

### Day 2

Goal: finish the basic custom Users REST API on top of the Day 1 H2 setup.

Planned approach:

- keep the same simple package structure from Day 1
- do not add unnecessary layers or complex patterns
- continue using the same local `User` entity
- make the API easy to test with browser, Postman, and later the frontend

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

---


### Important note for this project

Keep the design simple. This is a student project, so the goal is not enterprise architecture. The goal is to satisfy the assignment requirements clearly, with code that is easy to understand and easy to demo.
