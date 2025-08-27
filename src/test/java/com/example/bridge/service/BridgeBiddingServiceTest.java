package com.example.bridge.service;

import com.example.bridge.dto.BidRequest;
import com.example.bridge.dto.BidResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BridgeBiddingServiceTest {

    private final BridgeBiddingService service = new BridgeBiddingService();

    @Test
    void suggestBid_returnsSuggestionWithReasoning() {
        BidRequest req = new BidRequest(
                "AKQJ.T987.AK.QJ9",
                "N",
                "natural",
                "None",
                List.of("1C", "PASS", "1H", "PASS")
        );

        BidResponse resp = service.suggestBid(req);

        assertNotNull(resp, "response should not be null");
        assertNotNull(resp.suggestedBid());
        assertFalse(resp.suggestedBid().isBlank(), "should suggest a non-empty bid");
        assertNotNull(resp.explanation());
        assertFalse(resp.explanation().isBlank());
        assertTrue(resp.explanation().contains("HCP="), "explanation should include HCP");
        assertTrue(resp.explanation().contains("dist="), "explanation should include distribution summary");
    }
}
