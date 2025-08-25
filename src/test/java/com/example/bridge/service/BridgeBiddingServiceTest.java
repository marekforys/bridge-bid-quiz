package com.example.bridge.service;

import com.example.bridge.dto.BidRequest;
import com.example.bridge.dto.BidResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BridgeBiddingServiceTest {

    private final BridgeBiddingService service = new BridgeBiddingService();

    @Test
    void suggestBid_returnsPassWithExplanation_stub() {
        BidRequest req = new BidRequest(
                "AKQJ.T987.AK.QJ9",
                "N",
                "natural",
                "None",
                List.of("1C", "PASS", "1H", "PASS")
        );

        BidResponse resp = service.suggestBid(req);

        assertNotNull(resp, "response should not be null");
        assertEquals("PASS", resp.suggestedBid(), "default stub should return PASS");
        assertNotNull(resp.explanation());
        assertFalse(resp.explanation().isBlank());
    }
}
