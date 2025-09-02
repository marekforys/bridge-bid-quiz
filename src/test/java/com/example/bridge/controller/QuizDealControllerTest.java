package com.example.bridge.controller;

import com.example.bridge.entity.QuizDeal;
import com.example.bridge.model.HandPosition;
import com.example.bridge.service.QuizDealService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = QuizDealController.class)
class QuizDealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizDealService quizDealService;

    private QuizDeal sampleDeal(long id) {
        QuizDeal d = new QuizDeal();
        d.setId(id);
        d.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
        d.setDealer(HandPosition.EAST);
        d.setNorthHand("AKQJ.T987.AK.QJ9");
        d.setEastHand("KQ73.KJ3.Q98.QJ9");
        d.setSouthHand("QJ32.764.AKJ.832");
        d.setWestHand("A954.AQ2.7654.K4");
        d.setConvention("polish club");
        d.setAuctionJson("[\"1C\",\"PASS\"]");
        return d;
    }

    @Test
    @DisplayName("GET /api/deals/recent returns JSON list of recent deals")
    void listRecent_returnsJson() throws Exception {
        Mockito.when(quizDealService.listRecent(anyInt())).thenReturn(List.of(sampleDeal(1), sampleDeal(2)));

        mockMvc.perform(get("/api/deals/recent?limit=2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].dealer").value("E"))
                .andExpect(jsonPath("$[0].northHand").value("AKQJ.T987.AK.QJ9"));
    }

    @Test
    @DisplayName("GET /api/deals/recent.csv returns CSV content and attachment header")
    void downloadRecentCsv_returnsCsvAttachment() throws Exception {
        Mockito.when(quizDealService.listRecent(anyInt())).thenReturn(List.of(sampleDeal(1)));
        Mockito.when(quizDealService.toCsv(Mockito.anyList())).thenReturn(
                "id,createdAt,dealer,northHand,eastHand,southHand,westHand,convention,auctionJson\n" +
                "1,2024-01-01T00:00:00Z,E,AKQJ.T987.AK.QJ9,KQ73.KJ3.Q98.QJ9,QJ32.764.AKJ.832,A954.AQ2.7654.K4,polish club,\"[\\\"1C\\\",\\\"PASS\\\"]\"\n"
        );

        mockMvc.perform(get("/api/deals/recent.csv?limit=1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=")))
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(content().string(containsString("id,createdAt,dealer")))
                .andExpect(content().string(containsString("AKQJ.T987.AK.QJ9")));
    }
}
