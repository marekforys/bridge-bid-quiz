package com.example.bridge.controller;

import com.example.bridge.dto.BidRequest;
import com.example.bridge.dto.BidResponse;
import com.example.bridge.dto.CheckBidRequest;
import com.example.bridge.dto.CheckBidResponse;
import com.example.bridge.service.BridgeBiddingService;
import com.example.bridge.service.HandGeneratorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BidController.class)
class BidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BridgeBiddingService biddingService;

    @MockBean
    private HandGeneratorService handGeneratorService;

    @Test
    @DisplayName("GET /api/bids/ returns welcome HTML")
    void welcome_returnsHtml() throws Exception {
        mockMvc.perform(get("/api/bids/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bridge Bid Quiz API")));
    }

    @Test
    @DisplayName("POST /api/bids/suggest returns bid suggestion")
    void suggestBid_returnsResponse() throws Exception {
        Mockito.when(biddingService.suggestBid(any(BidRequest.class)))
                .thenReturn(new BidResponse("PASS", "stub"));

        String body = "{" +
                "\n  \"hand\": \"AKQJ.T987.AK.QJ9\"," +
                "\n  \"position\": \"N\"," +
                "\n  \"convention\": \"natural\"," +
                "\n  \"vulnerability\": \"None\"," +
                "\n  \"auction\": [\"1C\", \"PASS\"]\n}";

        mockMvc.perform(post("/api/bids/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.suggestedBid").value("PASS"))
                .andExpect(jsonPath("$.explanation").value("stub"));
    }

    @Test
    @DisplayName("POST /api/bids/suggest with missing convention returns 400")
    void suggestBid_validationError_missingConvention() throws Exception {
        String invalidBody = "{" +
                "\n  \"hand\": \"AKQJ.T987.AK.QJ9\"," +
                "\n  \"position\": \"N\"," +
                // convention is missing
                "\n  \"vulnerability\": \"None\"," +
                "\n  \"auction\": [\"1C\", \"PASS\"]\n}";

        mockMvc.perform(post("/api/bids/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/bids/check returns check result")
    void checkBid_returnsResponse() throws Exception {
        Mockito.when(biddingService.checkBid(any(CheckBidRequest.class)))
                .thenReturn(new CheckBidResponse("1C", "stub-check"));

        String body = "{" +
                "\n  \"proposedBid\": \"1C\"," +
                "\n  \"hand\": \"AKQJ.T987.AK.QJ9\"," +
                "\n  \"position\": \"N\"," +
                "\n  \"convention\": \"precision\"," +
                "\n  \"auction\": [\"PASS\", \"PASS\", \"PASS\"]\n}";

        mockMvc.perform(post("/api/bids/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.suggestedBid").value("1C"))
                .andExpect(jsonPath("$.explanation").value("stub-check"));
    }

    @Test
    @DisplayName("POST /api/bids/check with missing proposedBid returns 400")
    void checkBid_validationError_missingProposedBid() throws Exception {
        String invalidBody = "{" +
                // proposedBid is missing
                "\n  \"hand\": \"AKQJ.T987.AK.QJ9\"," +
                "\n  \"position\": \"N\"," +
                "\n  \"convention\": \"precision\"," +
                "\n  \"auction\": [\"PASS\", \"PASS\", \"PASS\"]\n}";

        mockMvc.perform(post("/api/bids/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }
}
