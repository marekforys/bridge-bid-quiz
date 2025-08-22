package com.example.bridge.controller;

import com.example.bridge.dto.BidRequest;
import com.example.bridge.dto.BidResponse;
import com.example.bridge.service.BridgeBiddingService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/bids", produces = MediaType.APPLICATION_JSON_VALUE)
public class BidController {

    private final BridgeBiddingService biddingService;

    public BidController(BridgeBiddingService biddingService) {
        this.biddingService = biddingService;
    }

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

    @PostMapping(path = "/suggest", consumes = MediaType.APPLICATION_JSON_VALUE)
    public BidResponse suggest(@Valid @RequestBody BidRequest request) {
        return biddingService.suggestBid(request);
    }
}
