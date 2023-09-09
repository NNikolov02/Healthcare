package com.example.healthcare.error;

import java.util.Map;

public class InvalidObjectException extends HealthcareApiException{

    private final Map<String, String> errors;

    public InvalidObjectException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

}
