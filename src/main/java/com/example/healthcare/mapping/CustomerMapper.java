package com.example.healthcare.mapping;

import com.example.healthcare.dto.customer.CustomerCreateRequest;
import com.example.healthcare.dto.customer.CustomerResponse;
import com.example.healthcare.dto.customer.CustomerUpdateRequest;
import com.example.healthcare.dto.doctor.DoctorCreateRequest;
import com.example.healthcare.dto.doctor.DoctorResponse;
import com.example.healthcare.dto.doctor.DoctorUpdateRequest;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    Customer modelFromCreateRequest(CustomerCreateRequest customerCreateDto);

    CustomerResponse responseFromModelOne(Customer customer);
    List<CustomerResponse> responseFromModelList(List<Customer> customers);


    @Mapping(target = "email",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "phoneNumber",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "address", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDto(CustomerUpdateRequest customerUpdateDto, @MappingTarget Customer customer);

}
