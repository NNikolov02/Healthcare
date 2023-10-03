package com.example.healthcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailableHours {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @JsonProperty("id")
    private UUID id;

    private String firstName;
    private String lastName;
    @Column(columnDefinition = "DATE")
    private LocalDate date;

    @ElementCollection
    @CollectionTable(name = "doctor_hours", joinColumns = @JoinColumn(name = "doctor_hours_id"))
    @Column(name = "hour")
    private List<String> hours;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @JsonManagedReference
    private Doctor doctor;


}

