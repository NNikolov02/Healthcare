package com.example.healthcare.mapping;

import com.example.healthcare.dto.appointment.AppointmentCreateRequest;
import com.example.healthcare.dto.appointment.AppointmentResponse;
import com.example.healthcare.dto.appointment.AppointmentUpdateRequest;
import com.example.healthcare.dto.customer.CustomerCreateRequest;
import com.example.healthcare.dto.customer.CustomerResponse;
import com.example.healthcare.dto.customer.CustomerUpdateRequest;
import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(uses = {DoctorMapperDto.class})
public interface AppointmentMapping {
    Appointment modelFromCreateRequest(AppointmentCreateRequest appointmentCreateDto);

    AppointmentResponse responseFromModelOne(Appointment appointment);
    List<AppointmentResponse> responseFromModelList(List<Appointment>appointments);


    @Mapping(target = "reason",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "startTime",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDto(AppointmentUpdateRequest appointmentUpdateDto, @MappingTarget Appointment appointment);

    public static String customersUrlFromCustomer(Customer customer) {


        if (customer != null) {

            return "http://localhost:8083/healthcare/customers/" + customer.getUsername();

        }

        return null;
    }
}
