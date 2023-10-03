package com.example.healthcare.dto.appointment;

import com.example.healthcare.dto.CustomerDto;
import com.example.healthcare.dto.DoctorDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AppointmentResponse {

    private UUID id;
    private LocalDate createTime;
    private LocalDate startDate;
    private String startTime;
    private LocalDateTime endTime;
    private String reason;
    private String url;
    private CustomerDto customer;
    private DoctorDto doctor;
}
