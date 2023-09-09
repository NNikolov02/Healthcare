package com.example.healthcare.mapping;

import com.example.healthcare.dto.DoctorDto;
import com.example.healthcare.model.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DoctorMapperDto {

    DoctorDto modelRoDto(Doctor doctor);

    Doctor dtoModel(Doctor doctorDto);
}
