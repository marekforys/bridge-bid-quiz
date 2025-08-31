package com.example.bridge.service;

import com.example.bridge.entity.QuizDeal;
import com.example.bridge.model.Deal;
import com.example.bridge.model.HandPosition;
import com.example.bridge.repository.QuizDealRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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

    public List<QuizDeal> listRecent(int limit) {
        int effectiveLimit = Math.max(1, Math.min(limit, 1000));
        return repository.findAll(
                PageRequest.of(0, effectiveLimit, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();
    }

    public String toCsv(List<QuizDeal> deals) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,createdAt,dealer,northHand,eastHand,southHand,westHand,convention,auctionJson\n");
        for (QuizDeal d : deals) {
            sb.append(safe(d.getId()))
              .append(',').append(safe(d.getCreatedAt()))
              .append(',').append(escape(d.getDealer() != null ? d.getDealer().name() : null))
              .append(',').append(escape(d.getNorthHand()))
              .append(',').append(escape(d.getEastHand()))
              .append(',').append(escape(d.getSouthHand()))
              .append(',').append(escape(d.getWestHand()))
              .append(',').append(escape(d.getConvention()))
              .append(',').append(escape(d.getAuctionJson()))
              .append('\n');
        }
        return sb.toString();
    }

    private String safe(Object o) { return o == null ? "" : o.toString(); }
    private String escape(String s) {
        if (s == null) return "";
        boolean mustQuote = s.contains(",") || s.contains("\n") || s.contains("\"");
        String t = s.replace("\"", "\"\"");
        return mustQuote ? ("\"" + t + "\"") : t;
    }
}
