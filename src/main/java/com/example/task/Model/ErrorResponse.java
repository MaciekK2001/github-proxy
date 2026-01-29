package com.example.task.Model;

public record ErrorResponse(
        int status,
        String message
) {}