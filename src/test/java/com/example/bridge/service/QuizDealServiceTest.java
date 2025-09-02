package com.example.bridge.service;

import com.example.bridge.entity.QuizDeal;
import com.example.bridge.model.HandPosition;
import com.example.bridge.repository.QuizDealRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class QuizDealServiceTest {

    private final QuizDealRepository repository = Mockito.mock(QuizDealRepository.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final QuizDealService service = new QuizDealService(repository, objectMapper);

    private QuizDeal sampleDeal(long id) {
        QuizDeal d = new QuizDeal();
        d.setId(id);
        d.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
        d.setDealer(HandPosition.WEST);
        d.setNorthHand("A,B\nC\"D");
        d.setEastHand("E");
        d.setSouthHand("F");
        d.setWestHand("G");
        d.setConvention("conv,\"x\"");
        d.setAuctionJson("[\"1C\",\"PASS\"]");
        return d;
    }

    @Test
    @DisplayName("toCsv escapes commas, quotes, and newlines")
    void toCsv_escapes() {
        String csv = service.toCsv(List.of(sampleDeal(1)));
        // header present
        assertThat(csv).startsWith("id,createdAt,dealer,northHand,eastHand,southHand,westHand,convention,auctionJson\n");
        // northHand with comma and newline should be quoted and quotes doubled
        assertThat(csv).contains("\"A,B\nC\"\"D\"" );
        // convention with comma and quotes should be quoted
        assertThat(csv).contains("\"conv,\"\"x\"\"\"");
    }

    @Test
    @DisplayName("listRecent caps limit and sorts by createdAt desc")
    void listRecent_sortsAndLimits() {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        Page<QuizDeal> page = new PageImpl<>(List.of(sampleDeal(1), sampleDeal(2)));
        when(repository.findAll(captor.capture())).thenReturn(page);

        List<QuizDeal> result = service.listRecent(5000); // will be capped to 1000
        assertThat(result).hasSize(2);

        Pageable pageable = captor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(1000);
        Sort sort = pageable.getSort();
        assertThat(sort.getOrderFor("createdAt")).isNotNull();
        assertThat(sort.getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);
    }
}
