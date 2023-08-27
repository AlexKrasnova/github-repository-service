package com.alexkrasnova.githubrepositoryservice.controller;

import com.alexkrasnova.githubrepositoryservice.dto.error.ErrorDTO;
import com.alexkrasnova.githubrepositoryservice.dto.error.ErrorType;
import com.alexkrasnova.githubrepositoryservice.exception.GithubUnavailableException;
import com.alexkrasnova.githubrepositoryservice.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorDTO> handleException(Exception exception, WebRequest request) {

        ErrorType errorType;
        if (exception instanceof UserNotFoundException) {
            errorType = ErrorType.USER_NOT_FOUND;
        } else if (exception instanceof HttpMediaTypeNotAcceptableException) {
            errorType = ErrorType.MEDIA_TYPE_NOT_ACCEPTABLE;
        } else if (exception instanceof GithubUnavailableException) {
            errorType = ErrorType.GITHUB_IS_UNAVAILABLE;
        } else {
            errorType = ErrorType.UNEXPECTED_ERROR;
        }
        log.error("An error has occurred while processing the request", exception);
        return process(errorType);
    }

    private ResponseEntity<ErrorDTO> process(ErrorType errorType) {
        ErrorDTO errorDTO = new ErrorDTO(errorType.getHttpStatus().value(), errorType.getDescription());
        return ResponseEntity.status(errorType.getHttpStatus()).body(errorDTO);
    }
}
