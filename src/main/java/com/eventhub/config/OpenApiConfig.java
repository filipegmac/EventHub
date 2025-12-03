package com.eventhub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class OpenApiConfig {


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gerenciamento de Eventos")  
                        .version("1.0.0")  
                        .description("Uma API simples para gerenciar eventos e participantes, com CRUD e inscrições. " +
                                "Usada para fins educacionais.")  
                        .termsOfService("http://example.com/terms")  
                        .license(new License()
                                .name("Apache 2.0")  
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(new Server()
                        .url("http://localhost:8080")  
                        .description("Servidor de Desenvolvimento")));
    }
}