package com.alexkrasnova.githubrepositoryservice.dto;

public record BranchDTO(
        String name,
        String lastCommitSha
) {
}
