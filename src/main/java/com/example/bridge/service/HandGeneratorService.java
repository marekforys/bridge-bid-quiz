package com.example.bridge.service;

import com.example.bridge.model.Card;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HandGeneratorService {
    
    public List<Card> generateRandomHand() {
        List<Card> deck = createDeck();
        Collections.shuffle(deck);
        return deck.stream().limit(13).collect(Collectors.toList());
    }
    
    private List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                deck.add(new Card(rank, suit));
            }
        }
        return deck;
    }
}
