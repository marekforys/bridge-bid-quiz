package com.example.bridge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 * The API documentation will be available at:
 * - Swagger UI: /swagger-ui.html
 * - OpenAPI JSON: /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server")
                ))
                .info(new Info()
                        .title("Bridge Bid Quiz API")
                        .version("1.0.0")
                        .description("""
                                ## Bridge Bid Quiz API
                                
                                This API provides endpoints for Bridge bidding suggestions, quizzes, and hand evaluation.
                                
                                ### Key Features:
                                - Generate random bridge hands
                                - Get bidding suggestions based on hand evaluation
                                - Create and take bridge bidding quizzes
                                - Evaluate bridge hands using standard point count systems
                                
                                ### API Status
                                - **Version**: 1.0.0
                                - **Environment**: Development
                                - **Contact**: [Your Name] <your.email@example.com>
                                """)
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@bridgebidquiz.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
