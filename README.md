# Bridge Bid Quiz

[![CI](https://github.com/marekforys/bridge-bid-quiz/actions/workflows/gradle.yml/badge.svg)](https://github.com/marekforys/bridge-bid-quiz/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/marekforys/bridge-bid-quiz/branch/main/graph/badge.svg)](https://codecov.io/gh/marekforys/bridge-bid-quiz)
[![OpenAPI Documentation](https://img.shields.io/badge/OpenAPI-Documentation-85EA2D?logo=swagger)](http://localhost:8080/swagger-ui.html)

Bridge bidding quiz system with automated bidding suggestions.

## Overview

Backend REST service built with Spring Boot (Java 17) and Gradle. It provides endpoints for bridge bidding suggestions, quizzes, and hand evaluation. The API is fully documented using OpenAPI 3.0 and can be explored interactively using the Swagger UI.

## API Documentation

The API is documented using OpenAPI 3.0. You can access the interactive documentation in several ways:

### Swagger UI
- **URL**: http://localhost:8080/swagger-ui.html
- **Features**:
  - Interactive API exploration
  - Try-it-out functionality for all endpoints
  - Request/response schemas
  - Authentication configuration (if applicable)

### OpenAPI JSON
- **URL**: http://localhost:8080/v3/api-docs
- **Format**: Raw OpenAPI 3.0 specification in JSON format
- **Usage**: Can be imported into API clients like Postman or used for code generation

## Requirements

- Java 17+
- Gradle (optional if you use the Gradle Wrapper)

## Getting Started

1. Generate Gradle Wrapper (if not present):
   - With a local Gradle installation:
     - Linux/macOS: `gradle wrapper`
     - Windows: `gradle wrapper`

   This will create `gradlew`, `gradlew.bat`, and the `.gradle/wrapper` files.

2. Build:
   - Linux/macOS: `./gradlew build`
   - Windows: `gradlew.bat build`

3. Run the app:
   - Linux/macOS: `./gradlew bootRun`
   - Windows: `gradlew.bat bootRun`

The service starts on `http://localhost:8080`.

## API Documentation Access

Once the application is running, you can access the API documentation at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Adding API Documentation to New Endpoints

To document a new endpoint, use the following annotations:

```java
@Operation(
    summary = "Get bidding suggestion",
    description = "Suggests the best bid based on the current hand and auction"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved bidding suggestion"),
    @ApiResponse(responseCode = "400", description = "Invalid input")
})
@PostMapping("/suggest")
public ResponseEntity<BidSuggestion> suggestBid(@RequestBody BidRequest request) {
    // Implementation
}
```

## Customizing the Documentation

You can customize the API documentation by modifying the `OpenApiConfig` class in the `config` package. This includes:
- API version information
- Server configurations
- Contact information
- License details
- Global security schemes

## Development

### Rebuilding API Documentation

The API documentation is automatically generated from the source code. After making changes to the API or its documentation, rebuild the project to update the documentation:

```bash
# Linux/macOS
./gradlew clean build

# Windows
gradlew.bat clean build
```

## Testing

- Run tests locally:

  ```bash
  # Linux/macOS
  ./gradlew test

  # Windows
  gradlew.bat test
  ```

- Run a single test class or method:

  ```bash
  ./gradlew test --tests "com.example.bridge.service.HandGeneratorServiceTest"
  ./gradlew test --tests "com.example.bridge.service.HandGeneratorServiceTest.generateDeal_returnsValidDealStructure"
  ```

- Test reports:
  - XML: `build/test-results/test/`
  - HTML: `build/reports/tests/test/index.html`

- Continuous Integration (GitHub Actions):
  - Workflow: `.github/workflows/gradle.yml`
  - Steps: runs `./gradlew test`, then `./gradlew build -x test`
  - Artifacts uploaded on every run: `test-reports` (contains XML and HTML reports)

## API

- POST `/api/bids/suggest`
  - Request body:

    ```json
    {
      "hand": "AKQJ.T987.AK.QJ9",
      "position": "N",
      "convention": "natural",
      "vulnerability": "None",
      "auction": ["1C", "PASS", "1H", "PASS"]
    }
    ```

  - Response body (example):

    ```json
    {
      "suggestedBid": "PASS",
      "explanation": "This is a placeholder suggestion. Implement bidding logic based on hand, position, vulnerability, and auction."
    }
    ```

  - Notes:
    - `convention` controls the bidding system used for suggestions. Examples: `natural`, `precision`, `polish club`. The current implementation uses a simple stub to demonstrate behavior.

## Project Structure

- `src/main/java/com/example/bridge/` — Application and packages
  - `BridgeBidQuizApplication.java` — Spring Boot entrypoint
  - `controller/BidController.java` — REST controller
  - `service/BridgeBiddingService.java` — Bidding logic stub
  - `dto/BidRequest.java`, `dto/BidResponse.java` — API DTOs
- `src/main/resources/application.properties` — App config
- `build.gradle`, `settings.gradle` — Gradle build files

## Notes

- Replace the stub logic in `BridgeBiddingService` with real evaluation and bidding rules.
