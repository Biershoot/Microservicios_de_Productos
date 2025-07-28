package com.alejandro.microservices.productservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI productServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .description("REST API for managing products in the microservices architecture")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Alejandro")
                                .email("alejandro@example.com")));
    }
} 