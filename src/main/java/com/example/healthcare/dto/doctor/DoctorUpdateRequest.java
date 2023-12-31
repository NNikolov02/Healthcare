package com.example.healthcare.dto.doctor;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorUpdateRequest {

    private String password;
    private String email;
    private String phoneNumber;

    private String hospitalName;
}
