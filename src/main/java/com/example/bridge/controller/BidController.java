package com.example.bridge.controller;

import com.example.bridge.dto.BidRequest;
import com.example.bridge.dto.BidResponse;
import com.example.bridge.dto.CheckBidRequest;
import com.example.bridge.dto.CheckBidResponse;
import com.example.bridge.dto.QuizHandResponse;
import com.example.bridge.model.HandPosition;
import com.example.bridge.service.BridgeBiddingService;
import com.example.bridge.service.HandGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling bridge bidding operations.
 * Provides endpoints for getting bidding suggestions based on the current hand and auction state.
 */

@RestController
@RequestMapping(path = "/api/bids", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(
    name = "Bid API",
    description = "Operations related to bridge bidding suggestions and analysis"
)
@SecurityRequirement(name = "bearerAuth")
public class BidController {

    private final BridgeBiddingService biddingService;
    private final HandGeneratorService handGeneratorService;

    public BidController(BridgeBiddingService biddingService, HandGeneratorService handGeneratorService) {
        this.biddingService = biddingService;
        this.handGeneratorService = handGeneratorService;
    }

    @Operation(
        summary = "Get a quiz hand",
        description = "Returns a stubbed bridge hand with position, convention and auction to drive the quiz UI"
    )
    @GetMapping("/quiz")
    public ResponseEntity<QuizHandResponse> getQuizHand() {
        var deal = handGeneratorService.generateDeal();

        // User always answers as North
        String northHand = deal.getHands().get(HandPosition.NORTH);

        // Convention used for simulation
        String convention = "polish club";

        // Build initial auction from Dealer up to but not including North
        java.util.List<String> auction = new java.util.ArrayList<>();
        HandPosition dealer = deal.getDealer();
        HandPosition cursor = dealer;
        while (cursor != HandPosition.NORTH) {
            String h = deal.getHands().get(cursor);
            String bid = biddingService.suggestOpeningBid(h, convention);
            auction.add(bid);
            cursor = cursor.next();
        }

        QuizHandResponse payload = new QuizHandResponse(
                northHand,
                "N",
                convention,
                auction
        );
        return ResponseEntity.ok(payload);
    }

    @Operation(
        summary = "Welcome page",
        description = "Returns a simple HTML welcome page with API documentation",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Welcome page HTML",
                content = @Content(mediaType = "text/html")
            )
        }
    )
    @GetMapping("/")
    public String welcome() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Bridge Bid Quiz API</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; }
                    code { background: #f4f4f4; padding: 2px 6px; border-radius: 3px; }\n                    pre { background: #f8f8f8; padding: 10px; border-radius: 5px; overflow-x: auto; }
                </style>
            </head>
            <body>
                <h1>Welcome to Bridge Bid Quiz API</h1>
                <p>This service provides bridge bidding suggestions based on the current hand and auction.</p>
                
                <h2>Available Endpoints</h2>
                
                <h3>POST /api/bids/suggest</h3>
                <p>Get a suggested bid based on the current hand and auction.</p>
                
                <h4>Example Request</h4>
                <pre>curl -X POST http://localhost:8080/api/bids/suggest \\
  -H "Content-Type: application/json" \\
  -d '{
    "hand": "AKQJ.T987.AK.QJ9",
    "position": "N",
    "convention": "natural",
    "vulnerability": "None",
    "auction": ["1C", "PASS", "1H", "PASS"]
  }'</pre>
                
                <h4>Example Response</h4>
                <pre>{
  "suggestedBid": "PASS",
  "explanation": "This is a placeholder suggestion. Implement bidding logic based on hand, position, vulnerability, and auction."
}</pre>
                
                <p>For more details, check the <a href="/swagger-ui.html">API documentation</a> (if Swagger is enabled).</p>
            </body>
            </html>
            """;
    }

    @Operation(
        summary = "Get a suggested bid",
        description = "Returns a suggested bid based on the current hand and auction state"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BidResponse.class),
                examples = @ExampleObject(
                    name = "Sample Response",
                    value = """
                    {
                      "suggestedBid": "PASS",
                      "explanation": "Stub based on convention='natural'. Implement full logic using hand, position, vulnerability, and auction."
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                      "timestamp": "2023-07-20T12:00:00.000+00:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Invalid hand format",
                      "path": "/api/bids/suggest"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/suggest")
    public ResponseEntity<BidResponse> suggestBid(
            @Parameter(description = "Bid request details", required = true)
            @Valid @RequestBody BidRequest request) {
        return ResponseEntity.ok(biddingService.suggestBid(request));
    }

    @Operation(
        summary = "Check a bid according to a convention",
        description = "Validates a proposed bid against the current hand, position, previous bids (auction), and bidding convention. The main field is 'proposedBid'."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successful operation",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CheckBidResponse.class),
                examples = @ExampleObject(
                    name = "Sample Response",
                    value = """
                    {
                      "suggestedBid": "1C",
                      "explanation": "Check stub for proposedBid='1C' based on convention='precision'. Implement rule validation against given auction."
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                    {
                      "timestamp": "2023-07-20T12:00:00.000+00:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Validation failed",
                      "path": "/api/bids/check"
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/check")
    public ResponseEntity<CheckBidResponse> checkBid(
            @Parameter(description = "Check bid request details", required = true)
            @Valid @RequestBody CheckBidRequest request) {
        return ResponseEntity.ok(biddingService.checkBid(request));
    }
}
