# bridge-bid-quiz
Bridge bidding quiz system

## Overview

Backend REST service built with Spring Boot (Java 17) and Gradle. It exposes an endpoint to suggest a bridge bid based on the hand and current auction. Current logic is a stub you can extend.

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

## API

- POST `/api/bids/suggest`
  - Request body:

    ```json
    {
      "hand": "AKQJ.T987.AK.QJ9",
      "position": "N",
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
