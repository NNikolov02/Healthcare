package com.example.healthcare.dto.doctor;

import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.dto.PhotoDto;
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
    private Boolean available;
   // private PhotoDto photo;
    private List<AvailableHoursDto> availableHours;
}
