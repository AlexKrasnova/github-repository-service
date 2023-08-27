package com.alexkrasnova.githubrepositoryservice.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Throwable t) {
        super("User not found.", t);
    }
}
