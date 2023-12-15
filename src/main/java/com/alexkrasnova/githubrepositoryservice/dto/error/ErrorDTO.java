package com.alexkrasnova.githubrepositoryservice.dto.error;

public record ErrorDTO(
        int status,
        String message
) {
}
