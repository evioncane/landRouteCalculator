# Land Route Calculator

A Spring Boot service that calculates the shortest land route between any two countries by traversing shared border data.

## How it works

Country border data is bundled as `countries.json` inside the JAR and loaded once at startup into an in-memory adjacency graph. Route queries are answered with **BFS** (Breadth-First Search), guaranteeing the fewest border crossings — O(V + E) time per query.

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 21+ |
| Maven | 3.9+ |
| Docker & Docker Compose | any recent version (optional) |

> **Java version:** The project requires Java 21. If you use [SDKMAN](https://sdkman.io/), run `sdk env` in the project root to activate the correct version automatically (`.sdkmanrc` is provided). Then use `./mvnw` — it also auto-detects Java 21 from SDKMAN.

## Build & Run

### Option 1 — Docker (recommended)

```bash
docker compose up --build
```

The image is built with a multi-stage Dockerfile (Maven builder → JRE runtime). The app starts on port **8081**.

### Option 2 — Maven wrapper

```bash
./mvnw spring-boot:run
```

Or build a JAR and run it:

```bash
./mvnw package -DskipTests
java -jar target/land-route-calculator-1.0.0.jar
```

## API

### `GET /routing/{origin}/{destination}`

Returns the shortest land route between two countries identified by their [cca3](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) codes.

**Success — 200 OK**

```
GET http://localhost:8081/routing/CZE/ITA
```

```json
{
  "route": ["CZE", "AUT", "ITA"]
}
```

**No land route — 400 Bad Request**

```
GET http://localhost:8081/routing/JPN/AUS
```

```json
{
  "message": "No land route found from JPN to AUS"
}
```

**Unknown country — 400 Bad Request**

```
GET http://localhost:8081/routing/XYZ/ITA
```

```json
{
  "message": "Country not found: XYZ"
}
```

### `GET /actuator/health`

Returns application health status.

### OpenAPI / Swagger

| URL | Description |
|-----|-------------|
| `http://localhost:8081/swagger-ui.html` | Interactive Swagger UI |
| `http://localhost:8081/v3/api-docs` | OpenAPI spec (JSON) |
| `http://localhost:8081/v3/api-docs.yaml` | OpenAPI spec (YAML) |

## Running Tests

```bash
./mvnw test
```

The test suite includes:

- **`RoutingServiceTest`** — unit tests for the BFS algorithm using an in-memory graph (no I/O)
- **`RoutingControllerTest`** — slice test for the REST layer using MockMvc with a mocked service
- **`CountryGraphServiceTest`** — verifies graph construction from the classpath resource
- **`RoutingIntegrationTest`** — full Spring Boot integration tests using `TestRestTemplate` against a real application context

To also run Checkstyle and SpotBugs static analysis (bound to the `verify` phase):

```bash
./mvnw verify
```

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | HTTP port (mapped to 8081 on the host via Docker) |
| `spring.threads.virtual.enabled` | `true` | Java 21 virtual threads |
