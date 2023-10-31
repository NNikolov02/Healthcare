package com.example.healthcare.web;

import com.example.healthcare.dto.appointment.AppointmentApiPage;
import com.example.healthcare.dto.appointment.AppointmentCreateRequest;
import com.example.healthcare.dto.appointment.AppointmentResponse;
import com.example.healthcare.dto.appointment.AppointmentUpdateRequest;
import com.example.healthcare.dto.doctor.DoctorAppointmentResponse;
import com.example.healthcare.dto.doctor.SetDoctorRequest;
import com.example.healthcare.error.InvalidObjectException;
import com.example.healthcare.error.NotFoundObjectException;
import com.example.healthcare.mapping.AppointmentMapping;
import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.registration.appointment.OnRegistrationCompleteEventApp;
import com.example.healthcare.registration.appointment.OnRegistrationCompleteEventAppDoc;
import com.example.healthcare.registration.customer.OnRegistrationCompleteEventCustomer;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.service.AppointmentService;
import com.example.healthcare.service.CustomerService;
import com.example.healthcare.service.DoctorService;
import com.example.healthcare.validation.ObjectValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/healthcare/appointments")
@AllArgsConstructor
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentMapping appointmentMapping;

    @Autowired
    private ObjectValidator validator;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private CustomerService customerService;

    @GetMapping(value = "", produces = "application/json")
    public AppointmentApiPage<AppointmentResponse> getAllAppointments(
            @RequestParam(required = false, defaultValue = "1") Integer currPage) {


        Page<AppointmentResponse> appointmentPage = appointmentService.fetchAll(currPage - 1, 10).map(appointmentMapping::responseFromModelOne);

        for (AppointmentResponse response : appointmentPage) {

            response.setUrl("http://localhost:8083/healthcare/appointments/" + response.getId());


        }
        return new AppointmentApiPage<>(appointmentPage);
    }

    @GetMapping(value = "/{appointmentId}")
    public ResponseEntity<AppointmentResponse> findById(@PathVariable String appointmentId) {

        Appointment appointment = appointmentService.findById(appointmentId);
        AppointmentResponse appointmentResponse = appointmentMapping.responseFromModelOne(appointment);
        appointmentResponse.setUrl("http://localhost:8083/healthcare/appointments/" + appointmentResponse.getId());

        return ResponseEntity.ok().body(appointmentResponse);
    }

    @GetMapping(value = "/customer/{customerName}")
    public ResponseEntity<AppointmentResponse> findByCustomerName(@PathVariable String customerName) {

        Appointment appointment = appointmentService.findByCustomerName(customerName);
        AppointmentResponse appointmentResponse = appointmentMapping.responseFromModelOne(appointment);
        appointmentResponse.setUrl("http://localhost:8083/healthcare/appointments/" + appointmentResponse.getId());

        return ResponseEntity.ok().body(appointmentResponse);
    }

    @DeleteMapping(value = "/{customerName}")
    public ResponseEntity<String> deleteByCustomerName(@PathVariable String customerName) {
        Customer existingCustomer = customerService.findByUserName(customerName);


        if (existingCustomer != null) {
            appointmentService.deleteByName(customerName);
            return ResponseEntity.ok("It is deleted!");

        }
        return ResponseEntity.ok("Cannot be deleted!");



    }

    @PostMapping("create/{name}")
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody AppointmentCreateRequest appointmentDto,HttpServletRequest request,
                                                                 @PathVariable String name) {
        Map<String, String> validationErrors = validator.validate(appointmentDto);
        if (validationErrors.size() != 0) {
            throw new InvalidObjectException("Invalid Appointment Create", validationErrors);
        }
        Customer existingCustomer = customerService.findByUserName(name);

        AppointmentResponse cartResponse = appointmentService.createApp(existingCustomer,appointmentDto,request);


            return ResponseEntity.status(201).body(cartResponse);

    }
    @PatchMapping("/{appointmentId}")
    public ResponseEntity<String>updateAppointment(@PathVariable String appointmentId, @RequestBody AppointmentUpdateRequest appointmentDto){
        Map<String, String> validationErrors = validator.validate(appointmentDto);
        if (validationErrors.size() != 0) {
            throw new InvalidObjectException("Invalid Appointment Update", validationErrors);

        }
        Appointment appointment = appointmentService.findById(appointmentId);
        String updateAppointment = appointmentService.updateAppointment(appointment,appointmentDto);

        //AppointmentResponse appointmentResponse =appointmentMapping.responseFromModelOne(saved);


        return ResponseEntity.ok().body(updateAppointment);

    }
    @PutMapping("/{appointmentId}")
    public ResponseEntity<String> chooseDoctor(@PathVariable String appointmentId, @RequestBody SetDoctorRequest doctorDto,HttpServletRequest request) {
        Appointment appointment1 = appointmentService.findById(appointmentId);
        Doctor doctor1 = doctorRepo.findByAppointmentId(UUID.fromString(appointmentId));
        String appointment = appointmentService.setAppointmentDoctor(appointment1,doctor1, doctorDto.getSetFistName()
                , doctorDto.getSetLastName(),doctorDto.getSetDate(),doctorDto.getSetTime(),request);


        return ResponseEntity.ok().body(appointment);
    }



}
