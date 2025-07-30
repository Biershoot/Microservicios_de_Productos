package com.alejandro.microservices.authservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI authServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("REST API for authentication and authorization in the microservices architecture")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Alejandro")
                                .email("alejandro@example.com")));
    }
} 