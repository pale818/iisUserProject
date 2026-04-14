# Code Explanation

This project exposes the same user data through four different communication technologies:
- **REST** — the standard way web apps talk to backends (JSON over HTTP)
- **SOAP** — an older, XML-based protocol used in enterprise systems
- **gRPC** — a modern binary protocol built for speed, used between services
- **GraphQL** — a flexible query language where the client decides what data it gets back

Each chapter below explains one of these technologies, what it does in this project, and exactly how a request travels from the browser through the code to the database and back.

---

---

# Chapter 1 — REST API, JWT Authentication, and User Roles

## What is REST?

REST (Representational State Transfer) is a convention for building web APIs using standard HTTP methods:
- `GET` — read data
- `POST` — create data
- `PUT` — update data
- `DELETE` — delete data

The client (browser, mobile app) sends an HTTP request to a URL. The server processes it and returns a JSON response. Every request is independent — the server does not remember previous requests (stateless).

---

## 1.1 JWT Authentication

### What is JWT?

JWT (JSON Web Token) is a way to prove who you are without the server storing session data. When you log in, the server creates a signed token and gives it to you. You include this token in every future request. The server verifies the signature to trust it — no database lookup needed for verification (only for roles, explained below).

A JWT looks like: `eyJhbGci...header.eyJzdWIi...payload.signature`

It has three parts separated by dots:
- **Header** — algorithm used to sign it
- **Payload** — data inside (username, expiry time)
- **Signature** — cryptographic proof it hasn't been tampered with

### Two types of tokens in this project

| Token | Expiry | Purpose |
|-------|--------|---------|
| Access token | 15 minutes | Sent with every API call in the `Authorization` header |
| Refresh token | 7 days | Stored in the DB, used only to get a new access token |

Short access token expiry limits damage if stolen — it stops working quickly. The refresh token lets the user stay logged in without re-entering their password.

---

### Login call flow

**Trigger:** User fills in username + password in the GUI and clicks Login.

```
Browser
  └─ POST /auth/login  { username, password }
       │
       ▼
AuthController.login()                         [auth/AuthController.java]
  └─ authManager.authenticate(username, password)
       │
       ▼
AppUserDetailsService.loadUserByUsername()     [auth/AppUserDetailsService.java]
  └─ AppUserRepository.findByUsername()        [auth/AppUserRepository.java]
       └─ SELECT * FROM app_users WHERE username = ?   [H2 database]
       └─ returns AppUser entity
  └─ converts role string to GrantedAuthority
       └─ "FULL_ACCESS" → ROLE_FULL_ACCESS
       └─ "READ_ONLY"   → ROLE_READ_ONLY
  └─ returns UserDetails object to Spring Security
       │
       ▼ (Spring Security checks password using BCrypt)
AuthController.login()  (continues)
  └─ JwtService.generateAccessToken(username)  [auth/JwtService.java]
       └─ builds JWT: subject=username, expiry=15min, signed with secret key
  └─ JwtService.generateRefreshToken(username)
       └─ builds JWT: subject=username, expiry=7days
  └─ AppUserRepository.save(user with refreshToken)
       └─ UPDATE app_users SET refresh_token = ? WHERE username = ?
  └─ returns AuthResponse { accessToken, refreshToken, role }
       │
       ▼
Browser receives:
  {
    "accessToken":  "eyJ...",
    "refreshToken": "eyJ...",
    "role": "FULL_ACCESS"
  }
  └─ stores accessToken and role in JS variables (memory only, not localStorage)
```

**Files involved:**
- `AuthController.java` — handles `POST /auth/login`, orchestrates the flow
- `AppUserDetailsService.java` — loads user from DB for Spring Security
- `AppUserRepository.java` — JPA interface, Spring generates the SQL automatically
- `JwtService.java` — creates and validates JWT tokens
- `AppUser.java` — the JPA entity (maps to `app_users` table in H2)
- `AuthRequest.java` — DTO (data transfer object) for the request body `{ username, password }`
- `AuthResponse.java` — DTO for the response `{ accessToken, refreshToken, role }`

---

### How every subsequent request is protected

After login, every request to `/api/**` must include the token. The `JwtFilter` intercepts every request before it reaches any controller.

```
Browser
  └─ GET /api/users
       Header: Authorization: Bearer eyJ...
       │
       ▼
JwtFilter.doFilterInternal()                   [auth/JwtFilter.java]
  └─ reads Authorization header
  └─ extracts token: "eyJ..."
  └─ JwtService.extractUsername(token)
       └─ decodes JWT payload → "admin"
  └─ JwtService.isTokenValid(token)
       └─ checks expiry timestamp in token payload
  └─ AppUserDetailsService.loadUserByUsername("admin")
       └─ AppUserRepository.findByUsername("admin")
            └─ SELECT * FROM app_users WHERE username = 'admin'
       └─ returns user with role ROLE_FULL_ACCESS
  └─ puts UsernamePasswordAuthenticationToken into SecurityContextHolder
       └─ this tells Spring Security "this request is authenticated as admin with ROLE_FULL_ACCESS"
       │
       ▼
SecurityConfig — authorization check                [config/SecurityConfig.java]
  └─ is this a GET /api/**?  → requires ROLE_READ_ONLY or ROLE_FULL_ACCESS → ALLOWED
  └─ is this a POST /api/**? → requires ROLE_FULL_ACCESS → check role → allow or 403
       │
       ▼
UserController (if allowed)                     [users/controller/UserController.java]
```

**Important:** The role is re-read from the database on every single request. The JWT only carries the username — the role always comes from the DB.

---

### Refresh token call flow

When the access token expires (after 15 min), the browser calls the refresh endpoint.

```
Browser
  └─ POST /auth/refresh
       Body: "eyJ..." (the refresh token string)
       │
       ▼
AuthController.refresh()
  └─ AppUserRepository.findByRefreshToken(token)
       └─ SELECT * FROM app_users WHERE refresh_token = ?
  └─ JwtService.isTokenValid(token)  — checks expiry
  └─ if found and valid:
       └─ JwtService.generateAccessToken(username)
       └─ returns new access token string
  └─ if not found or expired:
       └─ returns 401 Unauthorized with message "Invalid or expired refresh token"
```

---

## 1.2 User Roles

Two roles exist in this project:

| Role | Can do |
|------|--------|
| `READ_ONLY` | GET requests only — can read users, weather, GraphQL queries |
| `FULL_ACCESS` | All requests — can create, update, delete users, run mutations |

The rules are defined in `SecurityConfig.java`:
```java
.requestMatchers(HttpMethod.GET,    "/api/**").hasAnyRole("READ_ONLY", "FULL_ACCESS")
.requestMatchers(HttpMethod.POST,   "/api/**").hasRole("FULL_ACCESS")
.requestMatchers(HttpMethod.PUT,    "/api/**").hasRole("FULL_ACCESS")
.requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("FULL_ACCESS")
```

Two pre-seeded users are created at startup in `IisUsersProjectApplication.java`:
- `reader` / `reader123` → role `READ_ONLY`
- `admin` / `admin123` → role `FULL_ACCESS`

---

## 1.3 Custom REST CRUD API (Local H2 Database)

### What is JPA?

JPA (Jakarta Persistence API) is a standard for mapping Java objects to database tables. You write a Java class annotated with `@Entity` and Spring automatically creates the table and generates SQL for you. You never write `INSERT`, `UPDATE`, or `SELECT` manually.

### GET all users call flow

```
Browser
  └─ GET /api/users
       Header: Authorization: Bearer eyJ...
       │
       ▼
JwtFilter  (validates token, sets authentication)
       │
       ▼
UserController.getAllLocalUsers()              [users/controller/UserController.java]
  └─ UserService.getAllLocalUsers()            [users/service/UserService.java]
       └─ UserRepository.findAll()             [users/repository/UserRepository.java]
            └─ SELECT * FROM users            [H2 database]
            └─ returns List<User>
  └─ Spring serializes List<User> to JSON
       │
       ▼
Browser receives: [ { "id":1, "email":"...", ... }, ... ]
```

### POST create user call flow

```
Browser
  └─ POST /api/users
       Header: Authorization: Bearer eyJ... (must be FULL_ACCESS)
       Body: { "email":"x@x.com", "firstName":"X", ... }
       │
       ▼
JwtFilter → SecurityConfig (checks ROLE_FULL_ACCESS)
       │
       ▼
UserController.createLocalUser(@RequestBody User user)
  └─ UserService.saveLocalUser(user)
       └─ UserRepository.save(user)
            └─ INSERT INTO users (email, first_name, ...) VALUES (...)
            └─ returns saved User with generated id
  └─ returns HTTP 201 Created + saved user as JSON
```

### PUT update user call flow

```
Browser
  └─ PUT /api/users/3
       Header: Authorization: Bearer eyJ... (FULL_ACCESS)
       Body: { "email":"new@email.com", ... }
       │
       ▼
UserController.updateLocalUser(id=3, updatedUser)
  └─ UserService.updateLocalUser(3, updatedUser)
       └─ UserRepository.findById(3)
            └─ SELECT * FROM users WHERE id = 3
       └─ if not found → throws UserNotFoundException → 404 response
       └─ if found → copies new field values onto existing entity
       └─ UserRepository.save(existingUser)
            └─ UPDATE users SET email=?, first_name=? ... WHERE id=3
  └─ returns updated user as JSON
```

### DELETE user call flow

```
Browser
  └─ DELETE /api/users/3
       Header: Authorization: Bearer eyJ... (FULL_ACCESS)
       │
       ▼
UserController.deleteLocalUser(id=3)
  └─ UserService.deleteLocalUser(3)
       └─ UserRepository.findById(3)  — checks it exists first
       └─ UserRepository.delete(user)
            └─ DELETE FROM users WHERE id = 3
  └─ returns { "message": "User deleted successfully." }
```

**Files involved in CRUD:**
- `UserController.java` — HTTP endpoints, maps URLs to methods
- `UserService.java` — business logic layer
- `UserRepository.java` — JPA interface, Spring generates all SQL
- `User.java` — entity class, maps to the `users` table
- `UserNotFoundException.java` — thrown when user not found
- `UserExceptionHandler.java` — catches the exception and returns HTTP 404

---

## 1.4 Fetching from ReqRes (Public API)

ReqRes is a public demo REST API that provides fake user data. This project fetches from it to populate the XML for SOAP, and to show a live external data source.

### GET public users call flow

```
Browser
  └─ GET /api/users/public?page=1
       │
       ▼
UserController.getPublicUsers(page=1)
  └─ UserService.getPublicUsers(1)
       └─ RestClient.get()
            └─ GET https://reqres.in/api/users?page=1
                 Header: x-api-key: ...
            └─ response JSON deserialized into ReqResUsersResponse
                 └─ ReqResUsersResponse contains List<ReqResUserDto>
  └─ returns ReqResUsersResponse as JSON to browser
```

**Files involved:**
- `UserService.java` — uses Spring's `RestClient` to call the external API
- `ReqResUsersResponse.java` — DTO that maps the ReqRes JSON structure
- `ReqResUserDto.java` — DTO for a single ReqRes user

---

## 1.5 XML and JSON Validation + Save

### What is XSD / JSON Schema?

XSD (XML Schema Definition) is a file that describes the required structure of an XML document — which elements must exist, their types, whether they are required. If an XML document violates the XSD rules, validation fails.

JSON Schema does the same for JSON documents.

In this project, `user.xsd` and `user-schema.json` both define the same rule: a user must have `id`, `email`, `firstName`, `lastName`, and `avatar` — all required.

### XML validate + save call flow

```
Browser
  └─ POST /api/xml/validate-save
       Header: Authorization: Bearer eyJ... (FULL_ACCESS)
       Body: <user><id>1</id><email>...</email>...</user>
       │
       ▼
XmlController.validateAndSave(xml)             [xml/XmlController.java]
  └─ XmlValidationService.validateUser(xml)    [xml/XmlValidationService.java]
       └─ loads user.xsd from classpath using SchemaFactory
       └─ parses the XML string
       └─ validates against the schema
       └─ collects any validation error messages
       └─ returns List<String> errors (empty = valid)
  └─ if errors not empty:
       └─ returns HTTP 400 { "errors": ["missing element firstName", ...] }
  └─ if valid:
       └─ XmlValidationService.parseToUser(xml)
            └─ uses JAXB (Jakarta XML Bind) to unmarshal XML → User object
       └─ UserService.saveLocalUser(user)
            └─ UserRepository.save(user)
                 └─ INSERT INTO users ...
       └─ returns HTTP 201 + saved user
```

### JSON validate + save call flow

```
Browser
  └─ POST /api/json/validate-save
       Body: { "id":1, "email":"...", ... }
       │
       ▼
JsonController.validateAndSave(json)           [json/JsonController.java]
  └─ JsonValidationService.validate(json)      [json/JsonValidationService.java]
       └─ loads user-schema.json from classpath
       └─ uses networknt json-schema-validator library
       └─ validates the JSON string against the schema
       └─ returns List<String> errors
  └─ if errors:
       └─ returns HTTP 400 { "errors": [...] }
  └─ if valid:
       └─ parses JSON string into User using ObjectMapper
       └─ UserService.saveLocalUser(user)
       └─ returns HTTP 201 + saved user
```

**Files involved:**
- `XmlController.java` — REST endpoint for XML operations
- `XmlValidationService.java` — validates XML against `user.xsd`, parses to User via JAXB
- `JsonController.java` — REST endpoint for JSON operations
- `JsonValidationService.java` — validates JSON against `user-schema.json`
- `user.xsd` — XSD schema file in `src/main/resources/`
- `user-schema.json` — JSON Schema file in `src/main/resources/`

---

---

# Chapter 2 — SOAP, XML Generation, and Jakarta Validation

## What is SOAP?

SOAP (Simple Object Access Protocol) is an older protocol for web services. Unlike REST which uses plain HTTP + JSON, SOAP uses strictly structured XML envelopes. Every SOAP message looks like:

```xml
<soapenv:Envelope>
  <soapenv:Body>
    <tns:searchUsersRequest>
      <searchTerm>george</searchTerm>
    </tns:searchUsersRequest>
  </soapenv:Body>
</soapenv:Envelope>
```

SOAP services are described by a WSDL file (Web Services Description Language) — a formal XML contract that says what operations exist, what inputs they take, and what they return. Think of it like an API specification, but in XML format.

SOAP is still used in banking, government, and legacy enterprise systems. REST has largely replaced it for new projects, but interoperability with SOAP systems is still a required skill.

---

## 2.1 WebServiceConfig — How SOAP is registered

Spring Boot's normal web layer (MVC) handles REST at `/`. SOAP requires a separate servlet because it processes XML envelopes differently.

`WebServiceConfig.java` does three things:
1. Registers a `MessageDispatcherServlet` at `/ws/*` — this is the SOAP-specific servlet
2. Reads `users-soap.xsd` and auto-generates a WSDL file from it
3. Serves the WSDL at `http://localhost:8080/ws/users.wsdl`

The WSDL is generated automatically — you define the XSD and Spring WS builds the WSDL from it.

---

## 2.2 What is a namespace?

XML namespaces (`http://algebra.hr/soap/users`) are unique identifier strings — they are **not** real URLs that the browser fetches. They work like Java package names (`hr.algebra.iisusers`) — a way to avoid naming conflicts when different XML vocabularies are combined.

The namespace must match in three places:
- `users-soap.xsd` → `targetNamespace="http://algebra.hr/soap/users"`
- `UserSoapService.java` → `@PayloadRoot(namespace = "http://algebra.hr/soap/users")`
- The SOAP envelope sent by the browser → `xmlns:tns="http://algebra.hr/soap/users"`

If any of these don't match, Spring WS can't route the SOAP message to the right method.

---

## 2.3 XML Generation call flow

Before SOAP search can work, the backend needs an XML file containing users. This is generated from the ReqRes public API.

```
Browser
  └─ GET /api/xml/generate?page=1
       Header: Authorization: Bearer eyJ...
       │
       ▼
XmlController.generate(page=1)                 [xml/XmlController.java]
  └─ XmlGenerationService.generateFromReqRes(1) [xml/XmlGenerationService.java]
       └─ UserService.getPublicUsers(1)
            └─ GET https://reqres.in/api/users?page=1
            └─ returns List<ReqResUserDto>
       └─ DocumentBuilder creates an in-memory XML document
            └─ creates root element <users>
            └─ for each user → creates <user> with child elements:
                 <id>, <email>, <firstName>, <lastName>, <avatar>
       └─ Transformer serializes the document to an XML string
       └─ stores the string in lastGeneratedXml field (in-memory)
       └─ returns the XML string
  └─ returns XML as text/xml response
```

The generated XML looks like:
```xml
<users>
  <user>
    <id>1</id>
    <email>george.bluth@reqres.in</email>
    <firstName>George</firstName>
    <lastName>Bluth</lastName>
    <avatar>https://reqres.in/img/faces/1-image.jpg</avatar>
  </user>
  ...
</users>
```

This XML is stored in memory in `XmlGenerationService.lastGeneratedXml`. The SOAP service and Jakarta validation both read from this stored string — no repeated API calls needed.

---

## 2.4 SOAP Search with XPath call flow

XPath (XML Path Language) is a query language for XML — like SQL but for XML documents. Instead of `SELECT * FROM users WHERE firstName LIKE 'george'` you write `//user[contains(firstName,'george')]`.

```
Browser
  └─ POST /ws
       Header: Content-Type: text/xml
       Body: SOAP envelope with <searchTerm>george</searchTerm>
       │
       ▼
MessageDispatcherServlet  (Spring WS servlet at /ws/*)
  └─ reads the SOAP envelope
  └─ extracts the payload: <tns:searchUsersRequest>
  └─ matches namespace + localPart to find the right @Endpoint method
       │
       ▼
UserSoapService.search(SearchUsersRequest)     [soap/UserSoapService.java]
  └─ JAXB unmarshal: <searchUsersRequest> → SearchUsersRequest object
       └─ SearchUsersRequest.searchTerm = "george"
  └─ XmlGenerationService.getLastGeneratedXml()
       └─ returns the stored XML string (or generates fresh if null)
  └─ DocumentBuilder.parse(xml)
       └─ parses XML string into a DOM Document object
  └─ XPathFactory.newInstance().newXPath()
  └─ builds XPath expression:
       //user[
         contains(translate(firstName,'ABC...Z','abc...z'),'george')
         or
         contains(translate(lastName,'ABC...Z','abc...z'),'george')
       ]
       └─ translate() converts to lowercase (XPath 1.0 has no lower-case() function)
  └─ xpath.evaluate(expression, doc, NODESET)
       └─ returns NodeList of matching <user> elements
  └─ Transformer serializes each matched node to XML string
  └─ wraps results: <matchedUsers><user>...</user></matchedUsers>
  └─ sets result on SearchUsersResponse object
       │
       ▼
Spring WS wraps response in SOAP envelope and returns it
       │
       ▼
Browser receives SOAP response, extracts <result> content, displays it
```

**Files involved:**
- `UserSoapService.java` — the `@Endpoint` that handles the SOAP operation
- `SearchUsersRequest.java` — JAXB-annotated class for the request, maps `<searchUsersRequest>`
- `SearchUsersResponse.java` — JAXB-annotated class for the response
- `WebServiceConfig.java` — registers the SOAP servlet, generates WSDL
- `users-soap.xsd` — XSD that defines the request/response structure
- `XmlGenerationService.java` — provides the XML to search against

---

## 2.5 Jakarta XML Validation call flow

Jakarta XML Bind (JAXB) is the Java standard for converting between XML and Java objects. It can also validate XML against an XSD schema during the conversion process, collecting any rule violations as `ValidationEvent` messages.

```
Browser
  └─ GET /api/xml/jakarta-validate
       Header: Authorization: Bearer eyJ...
       │
       ▼
XmlController.jakartaValidate()
  └─ XmlGenerationService.getLastGeneratedXml()
       └─ returns the stored XML string
       └─ if null → returns 400 "Call generate first"
  └─ XmlValidationService.validateUsers(xml)    [xml/XmlValidationService.java]
       └─ SchemaFactory loads user.xsd from classpath
       └─ JAXBContext.newInstance(UsersJaxb.class)
            └─ UsersJaxb is a JAXB-annotated class mirroring the <users> XSD structure
       └─ Unmarshaller.setSchema(schema)
            └─ attaches the XSD schema to the unmarshaller
       └─ unmarshaller.unmarshal(xml)
            └─ as it parses each element, validates against XSD rules
            └─ any violation triggers a ValidationEvent
       └─ ValidationEventCollector collects all events as strings
       └─ returns List<String> errors
  └─ if errors empty → returns { "message": "Generated XML is valid" }
  └─ if errors found → returns { "errors": [...] }
```

**Files involved:**
- `XmlValidationService.java` — handles both single `<user>` and `<users>` collection validation
- `UserJaxb.java` — JAXB-annotated class representing a single `<user>` element
- `UsersJaxb.java` — JAXB-annotated class representing the `<users>` root element
- `user.xsd` — the XSD schema used for validation

---

---

# Chapter 3 — gRPC and Protocol Buffers

## What is gRPC?

gRPC (Google Remote Procedure Call) is a modern communication protocol built on HTTP/2. Instead of calling a REST URL and getting JSON back, gRPC lets you call a method on a remote server as if it were a local method call.

Key differences from REST:

| | REST | gRPC |
|--|------|------|
| Protocol | HTTP/1.1 | HTTP/2 |
| Data format | JSON (text) | Protobuf (binary) |
| Contract | No enforced schema | Strict schema (.proto file) |
| Speed | Slower (text parsing) | Faster (binary, compressed) |
| Use case | Public APIs, browsers | Service-to-service, performance-critical |

gRPC is used heavily in microservices (Google, Netflix) where many internal services talk to each other at high speed.

---

## What is Protocol Buffers (Protobuf)?

Protobuf is a binary serialization format. You define your data structure in a `.proto` file:

```proto
message WeatherResponse {
    string station = 1;
    string temperature = 2;
}
```

The `protobuf-maven-plugin` reads this file at build time and **auto-generates Java classes** in `target/generated-sources/`. You never write these classes manually — they are regenerated every time you build.

The numbers (`= 1`, `= 2`) are field identifiers used in the binary encoding — they are not values.

---

## 3.1 How the .proto file maps to Java code

`weather.proto` defines:

```proto
service WeatherService {
    rpc GetWeather (WeatherRequest) returns (WeatherListResponse);
}
```

After `mvn clean compile`, the plugin generates:
- `WeatherProto.java` — contains inner classes for `WeatherRequest`, `WeatherResponse`, `WeatherListResponse`
- `WeatherServiceGrpc.java` — contains the base class `WeatherServiceGrpc.WeatherServiceImplBase`

Your `WeatherGrpcService.java` extends this generated base class and overrides `getWeather()`. The generated code handles all the binary serialization, HTTP/2 framing, and network transport — you only write the business logic.

---

## 3.2 Weather gRPC call flow

Because browsers cannot speak gRPC natively, the project includes a REST wrapper (`WeatherRestController`) that translates between HTTP/JSON and the internal gRPC service.

```
Browser
  └─ GET /api/weather?station=Zagreb
       Header: Authorization: Bearer eyJ...
       │
       ▼
JwtFilter (validates token)
       │
       ▼
WeatherRestController.getWeather(station="Zagreb")  [grpc/WeatherRestController.java]
  └─ WeatherFetchService.fetchWeather("Zagreb")      [grpc/WeatherFetchService.java]
       └─ RestTemplate.getForObject("http://vrijeme.hr/hrvatska_n.xml", byte[].class)
            └─ HTTP GET to DHMZ server → returns raw XML bytes
       └─ DocumentBuilder.parse(xmlBytes)
            └─ parses XML into DOM Document
       └─ doc.getElementsByTagName("Grad")
            └─ gets all <Grad> (station) elements
       └─ for each station:
            └─ directText(el, "GradIme")  → station name
            └─ if name.toLowerCase().contains("zagreb"):
                 └─ directText(podatci, "Temp")    → temperature
                 └─ directText(podatci, "Vrijeme") → description
                 └─ builds WeatherData record
                 └─ adds to matches list
       └─ returns List<WeatherData> (all matching stations)
  └─ Spring serializes List<WeatherData> to JSON
       │
       ▼
Browser receives:
  [
    { "station": "Zagreb-Maksimir", "temperature": "14.2 °C",
      "description": "umjereno oblačno", "timestamp": "09.04.2026 17:00" },
    { "station": "Zagreb-Grič", ... }
  ]
  └─ GUI renders as a table (one row per matching station)
```

### How WeatherGrpcService uses the same logic

`WeatherGrpcService` is the actual gRPC server running on port 9090. It uses the same `WeatherFetchService` and builds a protobuf response:

```
gRPC client (e.g. grpcurl tool)
  └─ calls GetWeather on port 9090 with WeatherRequest { station: "Zagreb" }
       │
       ▼
WeatherGrpcService.getWeather(request, responseObserver)  [grpc/WeatherGrpcService.java]
  └─ WeatherFetchService.fetchWeather("Zagreb")
       └─ (same flow as above — fetches DHMZ XML, finds all matches)
  └─ for each WeatherData in results:
       └─ builds WeatherProto.WeatherResponse (protobuf message)
  └─ builds WeatherProto.WeatherListResponse with all responses
  └─ responseObserver.onNext(listResponse)   — sends the binary response
  └─ responseObserver.onCompleted()          — signals end of response
```

**Files involved:**
- `weather.proto` — defines the service contract and message structures
- `WeatherGrpcService.java` — gRPC server implementation, extends generated base class
- `WeatherFetchService.java` — fetches and parses DHMZ XML, returns all matching stations
- `WeatherRestController.java` — REST wrapper so the GUI can call the weather logic over HTTP
- Generated by build: `WeatherProto.java`, `WeatherServiceGrpc.java`

---

---

# Chapter 4 — GraphQL

## What is GraphQL?

GraphQL is a query language for APIs invented by Facebook. Instead of having many fixed endpoints like REST (`/api/users`, `/api/users/1`, `/api/users/search`...), GraphQL has a **single endpoint** (`/graphql`) and the client sends a query describing exactly what data it wants.

**REST problem:** You call `GET /api/users` and get back all fields (id, email, firstName, lastName, avatar) even if you only need the email. This is called over-fetching.

**GraphQL solution:** You ask for exactly what you need:
```graphql
{
  users {
    id
    email
  }
}
```
And you get back only `id` and `email` — nothing else.

GraphQL has two operation types:
- **Query** — read data (like GET in REST)
- **Mutation** — write/change data (like POST/PUT/DELETE in REST)

---

## 4.1 The schema file

`src/main/resources/graphql/schema.graphqls` is the GraphQL contract. It defines:
- What types exist (`User`)
- What queries are available (`users`, `user(id)`)
- What mutations are available (`createUser`, `deleteUser`)

Spring Boot reads this file automatically at startup and sets up the GraphQL engine.

---

## 4.2 GraphQL query call flow

```
Browser
  └─ POST /graphql
       Header: Authorization: Bearer eyJ...
       Header: Content-Type: application/json
       Body: { "query": "{ users { id email firstName lastName } }" }
       │
       ▼
JwtFilter (validates token, sets authentication)
       │
       ▼
SecurityConfig — POST /graphql requires ROLE_READ_ONLY or ROLE_FULL_ACCESS
       │
       ▼
Spring GraphQL engine
  └─ parses the query string
  └─ matches "users" field to @QueryMapping method
       │
       ▼
UserGraphQLController.users()                  [graphql/UserGraphQLController.java]
  └─ UserService.getAllLocalUsers()
       └─ UserRepository.findAll()
            └─ SELECT * FROM users
            └─ returns List<User>
  └─ GraphQL engine filters the result — only returns id, email, firstName, lastName
       └─ avatar is NOT included because the query didn't ask for it
       │
       ▼
Browser receives:
  {
    "data": {
      "users": [
        { "id": "1", "email": "george.bluth@reqres.in",
          "firstName": "George", "lastName": "Bluth" },
        ...
      ]
    }
  }
```

---

## 4.3 GraphQL mutation call flow

```
Browser
  └─ POST /graphql
       Body: {
         "query": "mutation { createUser(input: {
           email: \"x@x.com\",
           firstName: \"X\",
           lastName: \"Y\",
           avatar: \"\"
         }) { id email } }"
       }
       │
       ▼
JwtFilter → SecurityConfig (POST /graphql → requires READ_ONLY or FULL_ACCESS)
       │
       ▼
Spring GraphQL engine
  └─ detects "mutation" keyword
  └─ matches "createUser" to @MutationMapping method
       │
       ▼
UserGraphQLController.createUser(input)
  └─ maps input Map to User entity fields
  └─ UserService.saveLocalUser(user)
       └─ UserRepository.save(user)
            └─ INSERT INTO users ...
            └─ returns saved User with generated id
  └─ GraphQL engine filters result — only returns id, email (as requested)
       │
       ▼
Browser receives:
  {
    "data": {
      "createUser": { "id": "7", "email": "x@x.com" }
    }
  }
```

---

## 4.4 Why @Controller and not @RestController?

`@RestController` adds `@ResponseBody` to every method, which tells Spring to serialize the return value directly as the HTTP response body. GraphQL has its own response serialization layer — if `@ResponseBody` is also applied, the two conflict and the response is serialized twice or incorrectly.

`@Controller` leaves the response handling to the GraphQL engine, which is what Spring GraphQL expects.

---

## 4.5 GraphiQL — the browser IDE for GraphQL

GraphiQL is a browser-based tool for exploring and testing GraphQL APIs. It is enabled in `application.properties`:

```properties
spring.graphql.graphiql.enabled=true
```

Access it at `http://localhost:8080/graphiql`. It provides autocomplete, schema documentation, and a query editor. Note: you need to add the `Authorization: Bearer TOKEN` header via a browser extension (e.g. ModHeader) since GraphiQL doesn't have a built-in header editor.

**Files involved:**
- `schema.graphqls` — defines all GraphQL types, queries, and mutations
- `UserGraphQLController.java` — Java methods that handle each query and mutation
- `UserService.java` — reused from REST, provides the data access logic
- `UserRepository.java` — reused from REST, JPA access to H2

---

---

# Summary — How the four services compare

| | REST | SOAP | gRPC | GraphQL |
|--|------|------|------|---------|
| Protocol | HTTP/1.1 | HTTP (with XML envelope) | HTTP/2 | HTTP/1.1 |
| Data format | JSON | XML | Binary (Protobuf) | JSON |
| Contract | Optional (OpenAPI) | Required (WSDL) | Required (.proto) | Required (schema) |
| Endpoint | Many URLs | One URL `/ws` | One service, many methods | One URL `/graphql` |
| Client flexibility | Fixed response shape | Fixed request/response | Fixed request/response | Client chooses fields |
| Used for | Standard web APIs | Legacy/enterprise | Service-to-service | Frontend with varied needs |
| In this project | Users CRUD + validation | User search with XPath | DHMZ weather | User queries + mutations |
