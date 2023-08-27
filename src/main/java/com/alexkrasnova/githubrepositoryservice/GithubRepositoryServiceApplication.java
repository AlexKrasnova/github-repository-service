package com.alexkrasnova.githubrepositoryservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan("com.alexkrasnova.githubrepositoryservice.configuration")
@OpenAPIDefinition(info = @Info(title = "Github repository API", version = "1.0", description = "User repositories information"))
@SpringBootApplication
public class GithubRepositoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubRepositoryServiceApplication.class, args);
    }

}
