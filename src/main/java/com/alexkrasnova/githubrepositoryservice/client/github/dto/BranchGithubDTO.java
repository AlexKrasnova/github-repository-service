package com.alexkrasnova.githubrepositoryservice.client.github.dto;

public record BranchGithubDTO(
        String name,
        CommitGithubDTO commit
) {
}
