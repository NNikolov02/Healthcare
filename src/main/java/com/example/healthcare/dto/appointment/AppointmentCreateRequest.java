package com.example.healthcare.dto.appointment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentCreateRequest {

    private String reason;
    private LocalDateTime startTime;
}
