package com.example.bridge.controller;

import com.example.bridge.model.Card;
import com.example.bridge.service.HandGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hands")
@Tag(name = "Hand Management", description = "APIs for managing bridge hands")
public class HandController {

    private final HandGeneratorService handGeneratorService;

    public HandController(HandGeneratorService handGeneratorService) {
        this.handGeneratorService = handGeneratorService;
    }

    @GetMapping("/random")
    @Operation(
        summary = "Get a random bridge hand",
        description = "Returns a random hand of 13 bridge cards"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully returned a random hand",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = Card.class))
        )
    )
    public ResponseEntity<List<Card>> getRandomHand() {
        return ResponseEntity.ok(handGeneratorService.generateRandomHand());
    }
}
