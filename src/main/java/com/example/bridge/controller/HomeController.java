package com.example.bridge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String welcome() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Bridge Bid Quiz API</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; }
                    code { background: #f4f4f4; padding: 2px 6px; border-radius: 3px; }
                    pre { background: #f8f8f8; padding: 10px; border-radius: 5px; overflow-x: auto; }
                    .container { max-width: 800px; margin: 0 auto; }
                </style>
            </head>
            <body>
                <div class="container">
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
                </div>
            </body>
            </html>
            """;
    }
}
