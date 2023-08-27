package com.alexkrasnova.githubrepositoryservice.client.github.dto;

public record RepositoryGithubDTO(String name, UserGithubDTO owner, boolean fork) {
}
