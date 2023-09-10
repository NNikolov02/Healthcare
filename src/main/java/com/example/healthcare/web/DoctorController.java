package com.example.healthcare.web;

import com.example.healthcare.dto.doctor.*;
import com.example.healthcare.error.InvalidObjectException;
import com.example.healthcare.mapping.DoctorMapper;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.registration.customer.OnDoctorCompleteEventCustomerAccept;
import com.example.healthcare.registration.customer.OnDoctorCompleteEventCustomerDecline;
import com.example.healthcare.registration.doctor.OnRegistrationCompleteEventDoctor;
import com.example.healthcare.repository.CustomerRepository;
import com.example.healthcare.service.DoctorService;
import com.example.healthcare.validation.ObjectValidator;
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
@RequestMapping("/healthcare/doctors")
@AllArgsConstructor
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DoctorMapper doctorMapper;
    @Autowired
    private ObjectValidator validator;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private  CustomerRepository customerRepo;


    @GetMapping(value = "", produces = "application/json")
    public DoctorApiPage<DoctorResponse> getAllCarts(
            @RequestParam(required = false, defaultValue = "1") Integer currPage ){


        Page<DoctorResponse> doctorPage = doctorService.fetchAll(currPage - 1, 10).map(doctorMapper::responseFromModelOne);


        return new DoctorApiPage<>(doctorPage);
    }

    @GetMapping(value ="/{doctorId}")
    public ResponseEntity<DoctorResponse>findById(@PathVariable String doctorId){

        Doctor doctor = doctorService.findById(doctorId);

        return ResponseEntity.ok(doctorMapper.responseFromModelOne(doctor));
    }

    @GetMapping(value ="/available/{available}")
    public ResponseEntity<List<DoctorResponse>>findTheAvailable(@PathVariable boolean available){

        List<Doctor>doctors = (List<Doctor>) doctorService.findByAvailable(available);

        return ResponseEntity.ok(doctorMapper.responseFromModelList(doctors));

    }

    @GetMapping(value ="/hospital/{hospitalName}")
    public ResponseEntity<List<DoctorResponse>>findByHospitalName(@PathVariable String hospitalName){
        List<Doctor>doctors = (List<Doctor>) doctorService.findByHospitalName(hospitalName);

       return ResponseEntity.ok(doctorMapper.responseFromModelList(doctors));
    }
    @GetMapping(value ="/specialty/{specialty}")
    public ResponseEntity<List<DoctorResponse>>findBySpecialty(@PathVariable String specialty){
        List<Doctor>doctors = (List<Doctor>) doctorService.findBySpecialty(specialty);

        return ResponseEntity.ok(doctorMapper.responseFromModelList(doctors));
    }
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<String>deleteById(@PathVariable String doctorId){

        doctorService.deleteById(doctorId);

        return ResponseEntity.ok("It is deleted!");
    }
    @DeleteMapping("/email/{email}")
    public ResponseEntity<String>deleteByEmail(@PathVariable String email){

        doctorService.deleteByEmail(email);

        return ResponseEntity.ok("It is deleted!");
    }
    @PostMapping("/registration")
    public ResponseEntity<String> createUserAndRegister(
            @RequestBody @Valid DoctorCreateRequest doctorDto,
            HttpServletRequest request, Errors errors)  {
        Map<String, String> validationErrors = validator.validate(doctorDto);
        if (validationErrors.size() != 0) {
            throw new InvalidObjectException("Invalid Doctor Create", validationErrors);
        }

        Doctor create = doctorMapper.modelFromCreateRequest(doctorDto);
        Doctor saved = doctorService.save(create);



        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEventDoctor(saved,
                request.getLocale(), appUrl));


        return new ResponseEntity<>("Registration Successfully!", HttpStatus.CREATED);

    }
    @PatchMapping(value ="/{doctorEmail}")
    public ResponseEntity<DoctorResponse>updateDoctor(@PathVariable String doctorEmail, @RequestBody DoctorUpdateRequest doctorDto){
        Map<String, String> validationErrors = validator.validate(doctorDto);
        if (validationErrors.size() != 0) {
            throw new InvalidObjectException("Invalid Doctor Create", validationErrors);
        }
        Doctor doctor = doctorService.findByEmail(doctorEmail);
        doctorMapper.updateModelFromDto(doctorDto,doctor);

        Doctor saved = doctorService.save(doctor);

       DoctorResponse doctorResponse = doctorMapper.responseFromModelOne(saved);

        return ResponseEntity.status(203).body(doctorResponse);
    }
    @PostMapping ("/accept/{userName}")
    public ResponseEntity<String> acceptApp(@PathVariable String userName, @RequestBody SetAccept accept, HttpServletRequest request) {
        Customer customer = customerRepo.findCustomersByDoctorUsername(userName); // Change to customerService
        Doctor doctor = doctorService.findByName(userName);

        if (customer != null && doctor != null) {
            if (accept.isSetAccept()) {
                // Activation logic
                doctor.setAvailable(false);
                String appUrl = request.getContextPath();
                eventPublisher.publishEvent(new OnDoctorCompleteEventCustomerAccept(customer, request.getLocale(), appUrl));
                return ResponseEntity.ok("The appointment is accepted");
            } else {
                String appUrl = request.getContextPath();
                eventPublisher.publishEvent(new OnDoctorCompleteEventCustomerDecline(customer, request.getLocale(), appUrl));

                return ResponseEntity.ok("The appointment is not accepted");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
