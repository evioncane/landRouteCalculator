package com.routing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Land Route Calculator API")
                        .description("Calculates the shortest land route between two countries via border crossings. "
                                + "Countries are identified by their ISO 3166-1 alpha-3 (cca3) codes.")
                        .version("1.0.0"));
    }
}
