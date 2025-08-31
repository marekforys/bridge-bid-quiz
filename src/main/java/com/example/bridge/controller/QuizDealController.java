package com.example.bridge.controller;

import com.example.bridge.entity.QuizDeal;
import com.example.bridge.service.QuizDealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(path = "/api/deals")
@Tag(name = "Quiz Deal Export", description = "List and download stored quiz deals")
public class QuizDealController {

    private final QuizDealService quizDealService;

    public QuizDealController(QuizDealService quizDealService) {
        this.quizDealService = quizDealService;
    }

    @GetMapping(path = "/recent", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "List recent stored quiz deals",
            description = "Returns up to 'limit' most recent stored quiz deals.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "List of deals",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = QuizDeal.class)))
            )
    )
    public ResponseEntity<List<QuizDeal>> listRecent(
            @Parameter(description = "Max number of deals to return (1-1000)")
            @RequestParam(name = "limit", defaultValue = "100") int limit
    ) {
        return ResponseEntity.ok(quizDealService.listRecent(limit));
    }

    @GetMapping(path = "/recent.csv", produces = "text/csv")
    @Operation(
            summary = "Download recent stored quiz deals as CSV",
            description = "Downloads up to 'limit' most recent stored quiz deals in CSV format."
    )
    public ResponseEntity<byte[]> downloadRecentCsv(
            @Parameter(description = "Max number of deals to return (1-1000)")
            @RequestParam(name = "limit", defaultValue = "100") int limit
    ) {
        List<QuizDeal> deals = quizDealService.listRecent(limit);
        String csv = quizDealService.toCsv(deals);
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        String filename = "quiz-deals-" + DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.now()) + ".csv";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(bytes.length)
                .body(bytes);
    }
}
