package com.example.healthcare.error;

import lombok.Getter;

@Getter
public class NotFoundObjectException extends HealthcareApiException{

    private final String objectClass;
    private final String id;

    public NotFoundObjectException(String message, String objectClass, String id){
        super(message);
        this.objectClass = objectClass;
        this.id = id;
    }



}
