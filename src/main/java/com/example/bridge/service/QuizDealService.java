package com.example.bridge.service;

import com.example.bridge.entity.QuizDeal;
import com.example.bridge.model.Deal;
import com.example.bridge.model.HandPosition;
import com.example.bridge.repository.QuizDealRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizDealService {

    private final QuizDealRepository repository;
    private final ObjectMapper objectMapper;

    public QuizDealService(QuizDealRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public QuizDeal save(Deal deal, String convention, List<String> auction) {
        QuizDeal entity = new QuizDeal();
        entity.setDealer(deal.getDealer());
        entity.setNorthHand(deal.getHands().get(HandPosition.NORTH));
        entity.setEastHand(deal.getHands().get(HandPosition.EAST));
        entity.setSouthHand(deal.getHands().get(HandPosition.SOUTH));
        entity.setWestHand(deal.getHands().get(HandPosition.WEST));
        entity.setConvention(convention);
        try {
            entity.setAuctionJson(objectMapper.writeValueAsString(auction));
        } catch (JsonProcessingException e) {
            // Fallback to a joined string if JSON serialization fails
            entity.setAuctionJson(String.join(",", auction));
        }
        return repository.save(entity);
    }
}
