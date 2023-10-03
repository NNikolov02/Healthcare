package com.example.healthcare.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AvailableHoursDto {

    private String firstName;
    private String lastName;
    private LocalDate date;
    private List<String> hours;
}
