package com.example.bridge.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
@Schema(description = "Represents a complete bridge deal with 4 hands and dealer information")
public class Deal {
    @Schema(description = "The dealer for this deal", example = "NORTH")
    private final HandPosition dealer;
    
    @Schema(description = "Map of positions to their respective hands")
    private final Map<HandPosition, String> hands;

    @Override
    public String toString() {
        return "Deal{" +
                "dealer=" + dealer +
                ", hands=" + hands +
                '}';
    }
}
