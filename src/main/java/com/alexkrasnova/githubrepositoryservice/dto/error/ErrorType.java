package com.alexkrasnova.githubrepositoryservice.dto.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorType {

    USER_NOT_FOUND("User not found.", HttpStatus.NOT_FOUND),
    MEDIA_TYPE_NOT_ACCEPTABLE("Unsupported 'Accept' header. Must accept 'application/json'.", HttpStatus.NOT_ACCEPTABLE),
    UNEXPECTED_ERROR("Unexpected server error.", HttpStatus.INTERNAL_SERVER_ERROR),
    GITHUB_IS_UNAVAILABLE("Github is unavailable.", HttpStatus.SERVICE_UNAVAILABLE);

    private String description;

    private HttpStatus httpStatus;

    ErrorType(String description, HttpStatus httpStatus) {
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
