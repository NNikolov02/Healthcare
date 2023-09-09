package com.example.healthcare.mapping;

import com.example.healthcare.dto.doctor.DoctorCreateRequest;
import com.example.healthcare.dto.doctor.DoctorResponse;
import com.example.healthcare.dto.doctor.DoctorUpdateRequest;
import com.example.healthcare.model.Doctor;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DoctorMapper {

    Doctor modelFromCreateRequest(DoctorCreateRequest doctorCreateDto);

    DoctorResponse responseFromModelOne(Doctor doctor);
    List<DoctorResponse> responseFromModelList(List<Doctor> doctors);


    @Mapping(target = "email",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "phoneNumber",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "hospitalName", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDto(DoctorUpdateRequest doctorUpdateDto, @MappingTarget Doctor doctor);

}
