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
                        .title("API de Gerenciamento de Eventos")  // Título da API exibido no Swagger.
                        .version("1.0.0")  // Versão da API.
                        .description("Uma API simples para gerenciar eventos e participantes, com CRUD e inscrições. " +
                                "Usada para fins educacionais.")  // Descrição detalhada.
                        .termsOfService("http://example.com/terms")  // Link para termos de serviço (opcional).
                        .license(new License()
                                .name("Apache 2.0")  // Licença da API.
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(new Server()
                        .url("http://localhost:8080")  // Servidor local para desenvolvimento.
                        .description("Servidor de Desenvolvimento")));
    }
}