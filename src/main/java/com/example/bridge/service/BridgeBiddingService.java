package com.example.bridge.service;

import com.example.bridge.dto.BidRequest;
import com.example.bridge.dto.BidResponse;
import org.springframework.stereotype.Service;

@Service
public class BridgeBiddingService {

    public BidResponse suggestBid(BidRequest request) {
        // TODO: Implement real bidding logic. For now, return a simple stub.
        String explanation = "This is a placeholder suggestion. Implement bidding logic based on hand, position, vulnerability, and auction.";
        return new BidResponse("PASS", explanation);
    }
}
