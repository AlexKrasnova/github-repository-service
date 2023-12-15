package com.alexkrasnova.githubrepositoryservice.controller;

import com.alexkrasnova.githubrepositoryservice.exception.GithubUnavailableException;
import com.alexkrasnova.githubrepositoryservice.exception.UserNotFoundException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonExceptionHandlerTest {

    private final CommonExceptionHandler commonExceptionHandler = new CommonExceptionHandler();

    private static Stream<Arguments> exceptionsStatusesAndMessages() {
        return Stream.of(
                Arguments.of(new UserNotFoundException(new RuntimeException()), 404, "User not found."),
                Arguments.of(new HttpMediaTypeNotAcceptableException(""), 406, "Unsupported 'Accept' header. Must accept 'application/json'."),
                Arguments.of(new GithubUnavailableException(new RuntimeException()), 503, "Github is unavailable."),
                Arguments.of(new RuntimeException(), 500, "Unexpected server error.")
        );
    }

    @MethodSource("exceptionsStatusesAndMessages")
    @ParameterizedTest
    public void shouldReturnAppropriateResponse(Exception e, int statusCode, String message) {
        // When
        var actual = commonExceptionHandler.handleException(e, null);

        // Then
        assertThat(actual.getStatusCode().value()).isEqualTo(statusCode);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().status()).isEqualTo(statusCode);
        assertThat(actual.getBody().message()).isEqualTo(message);
    }

}
