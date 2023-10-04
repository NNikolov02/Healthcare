package com.example.healthcare.mapping;

import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.dto.doctor.*;
import com.example.healthcare.model.AvailableHours;
import com.example.healthcare.model.Doctor;

import org.mapstruct.*;
import org.springframework.stereotype.Component;

import javax.print.Doc;
import java.util.List;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DoctorMapper {

    Doctor modelFromCreateRequest(DoctorCreateRequest doctorCreateDto);
    Doctor modelFromUploadRequest(DoctorCreateRequest doctorCreateDto);

    List<AvailableHoursDto> responseFromModelHours(List<AvailableHours> availableHours);

    DoctorResponse responseFromModelOne(Doctor doctor);
    List<DoctorResponse> responseFromModelList(List<Doctor> doctors);


    @Mapping(target = "email",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "phoneNumber",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "hospitalName", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDto(DoctorUpdateRequest doctorUpdateDto, @MappingTarget Doctor doctor);


    // Map the List<LocalTime> to List<Hour> in your DoctorHourss entity

}