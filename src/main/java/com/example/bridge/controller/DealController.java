package com.example.bridge.controller;

import com.example.bridge.model.Deal;
import com.example.bridge.service.HandGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deals")
@Tag(name = "Deal Management", description = "APIs for managing bridge deals")
public class DealController {

    private final HandGeneratorService handGeneratorService;

    public DealController(HandGeneratorService handGeneratorService) {
        this.handGeneratorService = handGeneratorService;
    }

    @GetMapping("/random")
    @Operation(
        summary = "Get a random bridge deal",
        description = "Returns a complete bridge deal with 4 hands and dealer information"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully returned a random deal",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Deal.class)
        )
    )
    public ResponseEntity<Deal> getRandomDeal() {
        return ResponseEntity.ok(handGeneratorService.generateDeal());
    }
}
