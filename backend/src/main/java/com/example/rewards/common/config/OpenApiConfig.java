package com.example.rewards.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${server.port:8080}") String serverPort) {
        return new OpenAPI()
                .info(new Info()
                        .title("Banking Rewards & Cashback Engine API")
                        .version("1.0.0")
                        .description("Production-grade RESTful API for real-time banking transaction rewards calculation, multi-rule evaluation pipeline, wallet ledgering, and refund reversals.")
                        .contact(new Contact()
                                .name("Fintech Engineering Team")
                                .email("engineering@rewards-platform.internal"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))
                .servers(List.of(
                        new Server().url("/").description("Current Server Context"),
                        new Server().url("http://localhost:" + serverPort).description("Local Development Server")
                ));
    }
}
