package com.example.bridge.service;

import com.example.bridge.model.Deal;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HandGeneratorServiceTest {

    private final HandGeneratorService service = new HandGeneratorService();

    @Test
    void generateRandomHand_returns13UniqueCards() {
        var hand = service.generateRandomHand();
        assertEquals(13, hand.size(), "hand must contain 13 cards");

        // Use string representation rank+suit for uniqueness
        Set<String> unique = new HashSet<>();
        hand.forEach(c -> unique.add(c.toString()));
        assertEquals(13, unique.size(), "all cards in hand must be unique");
    }

    @RepeatedTest(3)
    void generateDeal_returnsValidDealStructure() {
        Deal deal = service.generateDeal();
        assertNotNull(deal.getDealer(), "dealer should be set");

        Map<?, String> hands = deal.getHands();
        assertNotNull(hands);
        assertEquals(4, hands.size(), "should have 4 hands");
        hands.forEach((pos, handStr) -> {
            assertNotNull(handStr, "hand string cannot be null");
            assertTrue(handStr.contains("."), "hand should contain dot-separated suits");
            String[] suits = handStr.split("\\.", -1);
            assertEquals(4, suits.length, "hand must have 4 suits (S.H.D.C)");
            int totalCards = suits[0].length() + suits[1].length() + suits[2].length() + suits[3].length();
            assertEquals(13, totalCards, "each hand must have 13 cards");
        });
    }
}
