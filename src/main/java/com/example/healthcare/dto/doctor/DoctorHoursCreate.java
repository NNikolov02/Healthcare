package com.example.healthcare.dto.doctor;

import com.example.healthcare.dto.AvailableHoursDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorHoursCreate {

    private AvailableHoursDto availableHours;
}
