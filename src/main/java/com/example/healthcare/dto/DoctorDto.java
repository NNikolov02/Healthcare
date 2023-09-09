package com.example.healthcare.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorDto {
    private String firstName;
    private String lastName;
    private String specialty;
    private String hospitalName;
}
