package com.example.healthcare.dto.doctor;

import com.example.healthcare.dto.AvailableHoursDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DoctorCreateRequest {
    private String username;
    private String password;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String specialty;
    private String hospitalName;
    private boolean available;
    private List<AvailableHoursDto> availableHours;
}
