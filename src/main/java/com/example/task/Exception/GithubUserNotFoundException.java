package com.example.task.Exception;

import org.springframework.web.client.HttpClientErrorException;

public class GithubUserNotFoundException extends RuntimeException {
    public GithubUserNotFoundException(HttpClientErrorException.NotFound ex, String message) {
        super(message, ex);
    }
}
