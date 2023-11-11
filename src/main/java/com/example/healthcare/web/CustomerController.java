package com.example.healthcare.web;

import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.dto.SetRatingRequest;
import com.example.healthcare.dto.customer.CustomerApiPage;
import com.example.healthcare.dto.customer.CustomerCreateRequest;
import com.example.healthcare.dto.customer.CustomerResponse;
import com.example.healthcare.dto.customer.CustomerUpdateRequest;
import com.example.healthcare.error.InvalidObjectException;
import com.example.healthcare.mapping.CustomerMapper;
import com.example.healthcare.mapping.DoctorMapper;
import com.example.healthcare.model.*;
import com.example.healthcare.registration.customer.OnRegistrationCompleteEventCustomer;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.service.AppointmentService;
import com.example.healthcare.service.CustomerService;
import com.example.healthcare.validation.ObjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/healthcare/customers")
@AllArgsConstructor
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ObjectValidator validator;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private AppointmentService appointmentService;
//    @PreAuthorize("hasRole('ROLE_USER')")
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
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CustomerResponse>findById(@PathVariable String customerId){

        Customer customer = customerService.findById(customerId);
        CustomerResponse customerResponse = customerMapper.responseFromModelOne(customer);
        customerResponse.setUrl("http://localhost:8086/healthcare/customers/" + customerResponse.getUsername());

        return ResponseEntity.ok().body(customerResponse);
    }

    @GetMapping(value ="/name/{customerName}")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CustomerResponse>findByUserName(@PathVariable String customerName){

        Customer customer = customerService.findByUserName(customerName);
        CustomerResponse customerResponse = customerMapper.responseFromModelOne(customer);
        customerResponse.setUrl("http://localhost:8086/healthcare/customers/" + customerResponse.getUsername());

        return ResponseEntity.ok().body(customerResponse);
    }
    @GetMapping("/catalogHours/{doctorLastName}")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AvailableHoursDto>>findCatalog(@PathVariable String doctorLastName){
        Doctor doctor = doctorRepo.findByLastName(doctorLastName);
        List<AvailableHoursDto>availableHoursDtos = customerService.setAvailableHours(doctor);

        return ResponseEntity.ok().body(availableHoursDtos);
    }

    @DeleteMapping(value ="/{customerId}")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteById(@PathVariable String customerId){
        customerService.deleteById(customerId);

        return ResponseEntity.ok("It is deleted!");

    }
    @DeleteMapping(value ="/name/{customerName}")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String>deleteByName(@PathVariable String customerName){
        customerService.deleteByName(customerName);

        return ResponseEntity.ok("It is deleted!");
    }

    @PostMapping(value ="/registration")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> createUserAndRegister(
            @RequestBody @Valid CustomerCreateRequest customerDto,
            HttpServletRequest request, Errors errors)  {
        Map<String, String> validationErrors = validator.validate(customerDto);
        if (validationErrors.size() != 0) {
            throw new InvalidObjectException("Invalid Customer Create", validationErrors);
        }
        String connect = customerService.registerUser(customerDto,request);


        return  ResponseEntity.status(HttpStatus.CREATED).body(connect);

    }
    @PatchMapping(value ="/{customerName}")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String>updateCustomer(@PathVariable String customerName, @RequestBody CustomerUpdateRequest customerDto){
        Map<String, String> validationErrors = validator.validate(customerDto);
        if (validationErrors.size() != 0) {
            throw new InvalidObjectException("Invalid Customer Create", validationErrors);
        }
        Customer customer = customerService.findByUserName(customerName);
        String customerUpdate  = customerService.updateCustomer(customer,customerDto);

        //CustomerResponse customerResponse = customerMapper.responseFromModelOne(saved);

        return ResponseEntity.ok().body(customerUpdate);
    }
    @PutMapping("/{customerName}/rating/{doctorFirstName}/{doctorLastName}")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String>setRating(@PathVariable String customerName,@PathVariable String doctorLastName ,@PathVariable  String doctorFirstName, @RequestBody SetRatingRequest request){
        Doctor doctor = doctorRepo.findByFirstNameAndLastName(doctorFirstName,doctorLastName);
        Appointment appointment = appointmentService.findByCustomerName(customerName);
        String rating = customerService.rating(appointment,doctor,request);


        return ResponseEntity.ok().body(rating);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> performLogout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok("Logged out successfully!");
    }



}
