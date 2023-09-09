package com.example.healthcare.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String dateOfBirth;
    private String address;
}
