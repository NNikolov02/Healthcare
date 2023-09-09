package com.example.healthcare.error;

import lombok.Getter;

import java.util.UUID;

@Getter
public class HealthcareApiException extends RuntimeException {

    private final UUID errorId;

    public HealthcareApiException(String message) {
        super(message);
        this.errorId = UUID.randomUUID();
    }
}