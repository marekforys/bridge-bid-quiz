package com.example.bridge.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HandPosition {
    NORTH("N"),
    EAST("E"),
    SOUTH("S"),
    WEST("W");

    private final String symbol;

    HandPosition(String symbol) {
        this.symbol = symbol;
    }

    @JsonValue
    public String getSymbol() {
        return symbol;
    }

    public HandPosition next() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }
}
