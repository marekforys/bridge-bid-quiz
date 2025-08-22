package com.example.bridge.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a playing card in a standard 52-card deck")
public class Card {
    @Schema(description = "The rank of the card (e.g., ACE, KING, QUEEN)", example = "ACE")
    private final Rank rank;
    @Schema(description = "The suit of the card (SPADES, HEARTS, DIAMONDS, CLUBS)", example = "SPADES")
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank.getSymbol() + suit.getSymbol();
    }

    public enum Suit {
        SPADES("S"), HEARTS("H"), DIAMONDS("D"), CLUBS("C");

        private final String symbol;

        Suit(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public enum Rank {
        TWO("2"), THREE("3"), FOUR("4"), FIVE("5"), SIX("6"), SEVEN("7"),
        EIGHT("8"), NINE("9"), TEN("T"), JACK("J"), QUEEN("Q"), KING("K"), ACE("A");

        private final String symbol;

        Rank(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }
}
