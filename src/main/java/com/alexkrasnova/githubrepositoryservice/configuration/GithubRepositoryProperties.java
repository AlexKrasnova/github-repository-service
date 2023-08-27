package com.alexkrasnova.githubrepositoryservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "alexkrasnova.github")
public record GithubRepositoryProperties(
        String token,
        String githubUrl
) {
}
