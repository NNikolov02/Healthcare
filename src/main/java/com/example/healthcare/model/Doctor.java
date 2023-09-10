package com.example.healthcare.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @JsonProperty("id")
    private UUID id;

    private String username;
    private String password;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String url;

    private String specialty;
    private String hospitalName;
    private boolean available;



    @OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("doctor")
    private Appointment appointment;

}