package com.alexkrasnova.githubrepositoryservice.exception;

public class GithubUnavailableException extends RuntimeException {
    public GithubUnavailableException(Throwable t) {
        super("Github is unavailable.", t);
    }
}
