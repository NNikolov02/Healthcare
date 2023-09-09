package com.example.healthcare.dto.customer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerUpdateRequest {

    private String password;
    private String email;
    private String phoneNumber;
    private String address;
}
