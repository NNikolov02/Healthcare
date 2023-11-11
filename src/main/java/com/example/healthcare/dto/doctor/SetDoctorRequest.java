package com.example.healthcare.dto.doctor;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SetDoctorRequest {

    private String setFistName;
    private String setLastName;
    private LocalDate setDate;
    private String setTime;
}
