package com.example.healthcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetRatingRequest {

    @Range(min = 1, max = 5, message = "i like ratings from 1 to 5")
    private Integer rating;
}
