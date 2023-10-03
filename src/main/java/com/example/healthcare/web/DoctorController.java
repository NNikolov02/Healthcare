package com.example.healthcare.web;

import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.dto.doctor.*;
import com.example.healthcare.error.InvalidObjectException;
import com.example.healthcare.mapping.DoctorMapper;
import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.AvailableHours;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;

import com.example.healthcare.registration.customer.OnDoctorCompleteEventCustomerAccept;
import com.example.healthcare.registration.customer.OnDoctorCompleteEventCustomerDecline;
import com.example.healthcare.registration.doctor.OnRegistrationCompleteEventDoctor;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.CustomerRepository;
import com.example.healthcare.repository.DoctorRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    @Autowired
    private AppointmentRepository appointmentRepo;
    @Autowired
    private DoctorRepository doctorRepo;


    @GetMapping(value = "", produces = "application/json")
    public DoctorApiPage<DoctorResponse> getAllCarts(
            @RequestParam(required = false, defaultValue = "1") Integer currPage ){


        Page<DoctorResponse> doctorPage = doctorService.fetchAll(currPage - 1, 10).map(doctorMapper::responseFromModelOne);

        //for(DoctorResponse doctorResponse:doctorPage){

        //}


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
    @GetMapping("/catalogHours/{doctorUserName}")
    public ResponseEntity<List<AvailableHoursDto>>findCatalog(@PathVariable String doctorUserName){
        Doctor doctor = doctorService.findByName(doctorUserName);
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
        List<AvailableHours>doctorHours = create.getAvailableHours();


        for(AvailableHours availableHours:doctorHours) {
            availableHours.setDoctor(create);
            availableHours.setFirstName(create.getFirstName());
            availableHours.setLastName(create.getLastName());
            availableHours.setHours(availableHours.getHours());



        }

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

    @PostMapping ("/accept/{appointmentId}")
    public ResponseEntity<String> acceptApp(@PathVariable String appointmentId, @RequestBody SetAccept accept, HttpServletRequest request) {
        List<Customer> customers = (List<Customer>) customerRepo.findByAppointmentId(UUID.fromString(appointmentId)); // Change to customerService
        Doctor doctor = doctorRepo.findByAppointmentId(UUID.fromString(appointmentId));
        String firstName = doctor.getFirstName();
        String lastName = doctor.getLastName();
        List<Appointment> appointments = appointmentRepo.findAllById(UUID.fromString(appointmentId));
        for(Customer customer:customers) {

            if (customer != null && doctor != null) {
                if (accept.isSetAccept()) {
                    List<AvailableHours> availableHours = doctor.getAvailableHours();
                    for (AvailableHours availableHours1 : availableHours) {
                        for(Appointment appointment:appointments) {
                            if (availableHours1.getDate().equals(appointment.getStartDate()) && availableHours1.getHours().contains(appointment.getStartTime())) {
                                availableHours1.getHours().remove(appointment.getStartTime());
                                doctorService.save(doctor);
                                //appointment.setDoctor(doctor);
                                //appointmentRepo.save(appointment);

                            }
                        }
                    }
                    doctor.setAvailable(false);
                    doctorService.save(doctor);
                    String appUrl = request.getContextPath();
                    eventPublisher.publishEvent(new OnDoctorCompleteEventCustomerAccept(customer, request.getLocale(), appUrl));
                    return ResponseEntity.ok("The appointment is accepted");

                } else {
                    for (Appointment appointment : appointments) {

                        String appUrl = request.getContextPath();
                        eventPublisher.publishEvent(new OnDoctorCompleteEventCustomerDecline(customer, request.getLocale(), appUrl));
                        appointment.setDoctor(null);
                        appointment.setStartTime(null);
                        appointment.setStartDate(null);
                        appointmentRepo.save(appointment);

                        return ResponseEntity.ok("The appointment is not accepted");
                    }
                }
            }

            }
        return ResponseEntity.notFound().build();
    }

}
