package com.example.healthcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Photo {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    private String originalFilename;
    private String contentType;

    @OneToOne(mappedBy = "photo")
    @JsonIgnoreProperties("photo")
    private Doctor doctor;

    @Lob
    @Column(length = 20971520)
    private byte[] content;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        // Calculate the hash code based on the fields of the Photo class
        result = prime * result + ((id == null) ? 0 : id.hashCode());

        // Include other fields here for hash code calculation if needed

        return result;
    }
}
