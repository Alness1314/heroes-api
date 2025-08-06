package com.alness.gamesheroes.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Value("${swg.server.url}")
    private String serverUrl;

    @Value("${swg.server.description}")
    private String serverDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(getInfo())
                .addServersItem(new Server().url(serverUrl).description(serverDescription))
                .components(createComponents());
    }

    private Info getInfo() {
        return new Info()
                .title("Heroes API")
                .description(
                        "Application to manage heroes video games.")
                .version("1.0")
                .license(new License()
                        .name("Alness Zadro")
                        .url("https://github.com/Alness1314"));
    }

    private Components createComponents() {
        return new Components();
    }

}
