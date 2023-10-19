package com.example.healthcare.web;

import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.dto.customer.CustomerApiPage;
import com.example.healthcare.dto.customer.CustomerCreateRequest;
import com.example.healthcare.dto.customer.CustomerResponse;
import com.example.healthcare.dto.customer.CustomerUpdateRequest;
import com.example.healthcare.error.InvalidObjectException;
import com.example.healthcare.mapping.CustomerMapper;
import com.example.healthcare.mapping.DoctorMapper;
import com.example.healthcare.model.AvailableHours;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.registration.customer.OnRegistrationCompleteEventCustomer;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.service.CustomerService;
import com.example.healthcare.validation.ObjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/healthcare/customers")
@AllArgsConstructor
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private DoctorMapper doctorMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ObjectValidator validator;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping(value = "", produces = "application/json")
    public CustomerApiPage<CustomerResponse> getAllCarts(
            @RequestParam(required = false, defaultValue = "1") Integer currPage ){


        Page<CustomerResponse> customerPage = customerService.fetchAll(currPage - 1, 10).map(customerMapper::responseFromModelOne);

        for (CustomerResponse response : customerPage) {

            response.setUrl("http://localhost:8086/healthcare/customers/" + response.getUsername());


        }
        return new CustomerApiPage<>(customerPage);
    }



    @GetMapping(value ="/{customerId}")
    public ResponseEntity<CustomerResponse>findById(@PathVariable String customerId){

        Customer customer = customerService.findById(customerId);
        CustomerResponse customerResponse = customerMapper.responseFromModelOne(customer);
        customerResponse.setUrl("http://localhost:8086/healthcare/customers/" + customerResponse.getUsername());

        return ResponseEntity.ok().body(customerResponse);
    }

    @GetMapping(value ="/name/{customerName}")
    public ResponseEntity<CustomerResponse>findByUserName(@PathVariable String customerName){

        Customer customer = customerService.findByUserName(customerName);
        CustomerResponse customerResponse = customerMapper.responseFromModelOne(customer);
        customerResponse.setUrl("http://localhost:8086/healthcare/customers/" + customerResponse.getUsername());

        return ResponseEntity.ok().body(customerResponse);
    }
    @GetMapping("/catalogHours/{doctorLastName}")
    public ResponseEntity<List<AvailableHoursDto>>findCatalog(@PathVariable String doctorLastName){
        Doctor doctor = doctorRepo.findByLastName(doctorLastName);
        List<AvailableHours> hours = doctor.getAvailableHours();
        List<AvailableHoursDto> doctorHoursResponse = doctorMapper.responseFromModelHours(hours);
        for(AvailableHoursDto availableHoursDto:doctorHoursResponse) {
            for (AvailableHours availableHours : hours) {
                availableHoursDto.setDate(availableHours.getDate());
                availableHoursDto.setHours(availableHours.getHours());
            }
        }
        //doctorHoursResponse.setFirstName(doctor.getFirstName());
        // doctorHoursResponse.setLastName(doctor.getLastName());

        return ResponseEntity.ok().body(doctorHoursResponse);
    }

    @DeleteMapping(value ="{customerId}")
    public ResponseEntity<String> deleteById(@PathVariable String customerId){
        customerService.deleteById(customerId);

        return ResponseEntity.ok("It is deleted!");

    }
    @DeleteMapping(value ="/name/{customerName}")
    public ResponseEntity<String>deleteByName(@PathVariable String customerName){
        customerService.deleteByName(customerName);

        return ResponseEntity.ok("It is deleted!");
    }

    @PostMapping(value ="/registration",produces = "application/json", consumes = "application/json")

    public ResponseEntity<String> createUserAndRegister(
            @RequestBody @Valid CustomerCreateRequest customerDto,
            HttpServletRequest request, Errors errors)  {
        Map<String, String> validationErrors = validator.validate(customerDto);
        if (validationErrors.size() != 0) {
            throw new InvalidObjectException("Invalid Customer Create", validationErrors);
        }

        Customer create = customerMapper.modelFromCreateRequest(customerDto);
        Customer saved = customerService.save(create);



        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEventCustomer(saved,
                request.getLocale(), appUrl));

        //CustomerResponse customerResponse = customerMapper.responseFromModelOne(saved);




        return new ResponseEntity<>("Registration Successfully!", HttpStatus.CREATED);

    }
    @PatchMapping(value ="/{customerName}")
    public ResponseEntity<CustomerResponse>updateCustomer(@PathVariable String customerName, @RequestBody CustomerUpdateRequest customerDto){
        Map<String, String> validationErrors = validator.validate(customerDto);
        if (validationErrors.size() != 0) {
            throw new InvalidObjectException("Invalid Customer Create", validationErrors);
        }
        Customer customer = customerService.findByUserName(customerName);
        customerMapper.updateModelFromDto(customerDto,customer);

        Customer saved = customerService.save(customer);

        CustomerResponse customerResponse = customerMapper.responseFromModelOne(saved);

        return ResponseEntity.status(203).body(customerResponse);
    }




}
