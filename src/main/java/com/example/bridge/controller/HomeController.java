package com.example.bridge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String welcome() {
        // Redirect root to Swagger UI (springdoc exposes /swagger-ui.html which redirects to /swagger-ui/index.html)
        return "redirect:/swagger-ui.html";
    }
}
