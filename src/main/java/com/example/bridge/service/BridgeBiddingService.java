package com.example.bridge.service;

import com.example.bridge.dto.BidRequest;
import com.example.bridge.dto.BidResponse;
import org.springframework.stereotype.Service;

@Service
public class BridgeBiddingService {

    public BidResponse suggestBid(BidRequest request) {
        // TODO: Implement real bidding logic. For now, return a simple convention-based stub.
        String convention = request.convention() == null ? "" : request.convention().trim().toLowerCase();
        String suggested;
        switch (convention) {
            case "precision", "polish club", "polish-club", "polish", "strong club", "strong-club":
                suggested = "1C"; // strong club opening
                break;
            case "2/1", "two-over-one", "2-over-1":
                suggested = "1H"; // arbitrary stub for 2/1
                break;
            case "acol":
                suggested = "1H"; // arbitrary stub for Acol
                break;
            case "natural", "std american", "standard american", "sayc":
                suggested = "PASS"; // default natural suggestion
                break;
            default:
                suggested = "PASS"; // fallback
        }

        String explanation = "Stub based on convention='" + request.convention() + "'. Implement full logic using hand, position, vulnerability, and auction.";
        return new BidResponse(suggested, explanation);
    }
}
