package com.example.healthcare.dto.customer;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerCreateRequest {
    @Column(unique = true)
    private String username;

    private String password;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String dateOfBirth;
    private String address;
}
