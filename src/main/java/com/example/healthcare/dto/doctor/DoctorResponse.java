package com.example.healthcare.dto.doctor;

import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.dto.RatingDto;
import com.example.healthcare.model.Rating;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class DoctorResponse {

    private UUID id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String specialty;
    private String hospitalName;
    private boolean available;
    private UUID personPhotoIds;
    private List<AvailableHoursDto> availableHours;
    private Integer rating;
}
