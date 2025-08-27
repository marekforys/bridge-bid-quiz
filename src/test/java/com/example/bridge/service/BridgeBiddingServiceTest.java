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

    @Test
    void natural_balanced_16Hcp_opens1NT() {
        // 4-3-3-3, 16 HCP
        // S: AK73 (7), H: QJ3 (3), D: Q98 (2), C: KJ9 (4) => 16
        BidRequest req = new BidRequest(
                "AK73.QJ3.Q98.KJ9",
                "N",
                "natural",
                "None",
                List.of()
        );
        BidResponse resp = service.suggestBid(req);
        assertEquals("1NT", resp.suggestedBid());
    }

    @Test
    void natural_fiveCardMajor_opensThatMajor() {
        // Spades 5+ and 13 HCP => open 1S
        BidRequest req = new BidRequest(
                "AKQJ9.32.74.K865",
                "N",
                "natural",
                "None",
                List.of()
        );
        BidResponse resp = service.suggestBid(req);
        assertEquals("1S", resp.suggestedBid());
    }

    @Test
    void natural_noFiveCardSuit_betterMinor() {
        // Unbalanced 4-4-4-1, 13 HCP, no 5-card suit => open better minor (1D)
        BidRequest req = new BidRequest(
                "KQ32.KQ32.QJ92.7",
                "N",
                "natural",
                "None",
                List.of()
        );
        BidResponse resp = service.suggestBid(req);
        assertEquals("1D", resp.suggestedBid());
    }

    @Test
    void acol_opensFourCardMajor() {
        // 4-4 majors, expect 1S by tie-break order
        BidRequest req = new BidRequest(
                "KQ53.KJ72.A94.63",
                "N",
                "acol",
                "None",
                List.of()
        );
        BidResponse resp = service.suggestBid(req);
        assertEquals("1S", resp.suggestedBid());
    }

    @Test
    void precision_strongClub_16PlusHcp_opens1C() {
        // Balanced 16 HCP should open 1C in precision
        BidRequest req = new BidRequest(
                "AK73.QJ3.Q98.KJ9",
                "N",
                "precision",
                "None",
                List.of()
        );
        BidResponse resp = service.suggestBid(req);
        assertEquals("1C", resp.suggestedBid());
    }
}
