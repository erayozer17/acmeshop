package com.erayoezer.acmeshop.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final boolean authenticated;

    public CustomException(HttpStatus status, String message, boolean authenticated) {
        super(message);
        this.status = status;
        this.authenticated = authenticated;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public boolean getAuthenticated() {
        return authenticated;
    }
}
