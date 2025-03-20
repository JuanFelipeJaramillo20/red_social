package com.pantheon.redsocial.Atenea.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Atenea API Documentation")
                        .version("1.0.0")
                        .description("Documentación de la API del microservicio Atenea, que se encarga de manejar la autenticación de usuario y los perfiles.")
                        .contact(new Contact()
                                .name("Soporte o contacto")
                                .email("juanfelipejaramillolosada@gmail.com")
                                .url("https://github.com/JuanFelipeJaramillo20"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
