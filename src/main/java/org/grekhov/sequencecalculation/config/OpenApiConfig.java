package org.grekhov.sequencecalculation.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sequenceCalculationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sequence Calculation API")
                        .description("API для работы с dataset и вычислений")
                        .version("v1.0")
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("Документация проекта")
                        .url("https://github.com/your-repo"));
    }
}

