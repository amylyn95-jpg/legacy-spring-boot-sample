package com.techbookstore.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("techbookstore-api")
                .packagesToScan("com.techbookstore.app.controller")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI apiEndPointsInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("TechBookStore REST API")
                        .description("技術専門書店在庫管理システム REST API")
                        .version("1.0.0"));
    }
}
