package com.ashraf.payment.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "Bearer Authentication";

    private final OpenApiProperties properties;

    public OpenApiConfig(OpenApiProperties properties) {
        this.properties = properties;
    }

    @Bean
    OpenAPI customOpenAPI() {

        var securityScheme = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .info(new Info()
                        .title(properties.title())
                        .version(properties.version())
                        .description(properties.description()))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME, securityScheme))
                .addSecurityItem(new SecurityRequirement()
                        .addList(SECURITY_SCHEME));
    }
}