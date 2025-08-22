package com.example.bridge.service;

import com.example.bridge.model.Card;
import com.example.bridge.model.Deal;
import com.example.bridge.model.HandPosition;
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
    
    public Deal generateDeal() {
        List<Card> deck = createDeck();
        Collections.shuffle(deck);
        
        Map<HandPosition, List<Card>> hands = new EnumMap<>(HandPosition.class);
        HandPosition[] positions = HandPosition.values();
        
        // Initialize empty hands
        for (HandPosition position : positions) {
            hands.put(position, new ArrayList<>());
        }
        
        // Deal cards to each position in order
        for (int i = 0; i < deck.size(); i++) {
            HandPosition position = positions[i % 4];
            hands.get(position).add(deck.get(i));
        }
        
        // Sort each hand by suit and rank
        Map<HandPosition, String> formattedHands = new EnumMap<>(HandPosition.class);
        for (Map.Entry<HandPosition, List<Card>> entry : hands.entrySet()) {
            formattedHands.put(entry.getKey(), formatHand(entry.getValue()));
        }
        
        // Select random dealer
        HandPosition dealer = HandPosition.values()[
            new Random().nextInt(HandPosition.values().length)];
        
        return new Deal(dealer, formattedHands);
    }
    
    private String formatHand(List<Card> cards) {
        // Group cards by suit
        Map<Card.Suit, String> suits = new EnumMap<>(Card.Suit.class);
        for (Card.Suit suit : Card.Suit.values()) {
            suits.put(suit, "");
        }
        
        // Sort cards by rank (Ace high)
        cards.sort(Comparator
            .comparing(Card::getSuit)
            .thenComparing(c -> -c.getRank().ordinal()));
        
        // Build string for each suit
        for (Card card : cards) {
            suits.merge(card.getSuit(), card.getRank().getSymbol(), String::concat);
        }
        
        // Join suits with dots in S, H, D, C order
        return String.join(".", 
            suits.get(Card.Suit.SPADES),
            suits.get(Card.Suit.HEARTS),
            suits.get(Card.Suit.DIAMONDS),
            suits.get(Card.Suit.CLUBS)
        );
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
