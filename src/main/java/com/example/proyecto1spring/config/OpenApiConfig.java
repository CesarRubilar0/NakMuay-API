package com.example.proyecto1spring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Servidor de desarrollo");

        Info information = new Info()
                .title("NakMuay API")
                .version("1.0.0")
                .description("API REST para la gestión de Academia de Muay Thai. " +
                        "Permite administrar planes, membresías, horarios y usuarios.");

        return new OpenAPI()
                .servers(List.of(server))
                .info(information);
    }
}
