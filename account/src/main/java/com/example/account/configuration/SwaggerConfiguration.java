package com.example.account.configuration;

import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    //http://localhost:8081/swagger-ui/index.html
    @Value("http://localhost:8080")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact();
        contact.setEmail("dev@example.com");
        contact.setName("Dev");
        contact.setUrl("https://www.example.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Workout Planning API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to plan activities and workouts.").termsOfService("https://www.example.com/terms")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
