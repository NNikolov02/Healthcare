package com.example.healthcare.web;

import com.example.healthcare.dto.appointment.AppointmentApiPage;
import com.example.healthcare.dto.appointment.AppointmentCreateRequest;
import com.example.healthcare.dto.appointment.AppointmentResponse;
import com.example.healthcare.dto.appointment.AppointmentUpdateRequest;
import com.example.healthcare.dto.doctor.DoctorAppointmentResponse;
import com.example.healthcare.dto.doctor.SetDoctorRequest;
import com.example.healthcare.error.InvalidObjectException;
import com.example.healthcare.mapping.AppointmentMapping;
import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.Customer;
import com.example.healthcare.registration.appointment.OnRegistrationCompleteEventApp;
import com.example.healthcare.registration.appointment.OnRegistrationCompleteEventAppDoc;
import com.example.healthcare.registration.customer.OnRegistrationCompleteEventCustomer;
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
import java.util.Map;

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
    private DoctorService doctorService;
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

    @DeleteMapping(value = "/customer/{customerName}")
    public ResponseEntity<String> deleteByCustomerName(@PathVariable String customerName) {
        appointmentService.deleteByName(customerName);

        return ResponseEntity.ok("It is deleted!");
    }

    @PostMapping("create/{name}")
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody AppointmentCreateRequest appointmentDto,HttpServletRequest request,
                                                          @PathVariable String name) {
        Map<String, String> validationErrors = validator.validate(appointmentDto);
        if (validationErrors.size() != 0) {
            throw new InvalidObjectException("Invalid Appointment Create", validationErrors);
        }


        Customer existingCustomer = customerService.findByUserName(name);

        if (existingCustomer != null) {
            Appointment create = appointmentMapping.modelFromCreateRequest(appointmentDto);
            create.setCreateTime(LocalDate.now());
            create.setCustomer(existingCustomer);

            //existingCustomer.getCarts().add(create);

            Appointment saved = appointmentService.save(create);

            AppointmentResponse cartResponse = appointmentMapping.responseFromModelOne(saved);
            cartResponse.setCustomer("http://localhost:8083/healthcare/customers/name/" + existingCustomer.getUsername());

            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEventApp(saved,
                    request.getLocale(), appUrl));

            return ResponseEntity.status(201).body(cartResponse);
        }
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{customerName}")
    public ResponseEntity<AppointmentResponse>updateAppointment(@PathVariable String customerName, @RequestBody AppointmentUpdateRequest appointmentDto){
        Map<String, String> validationErrors = validator.validate(appointmentDto);
        if (validationErrors.size() != 0) {
            throw new InvalidObjectException("Invalid Appointment Update", validationErrors);

        }
        Appointment appointment = appointmentService.findByCustomerName(customerName);
        appointmentMapping.updateModelFromDto(appointmentDto,appointment);
        Appointment saved = appointmentService.save(appointment);

        AppointmentResponse appointmentResponse =appointmentMapping.responseFromModelOne(saved);


        return ResponseEntity.status(203).body(appointmentResponse);

    }
    @PutMapping("/{customerName}")
    public ResponseEntity<String> chooseDoctor(@PathVariable String customerName, @RequestBody SetDoctorRequest doctorDto,HttpServletRequest request){
        Appointment doctor = appointmentService.setAppointmentDoctor(customerName,doctorDto.getSetFistName(),doctorDto.getSetLastName());


        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEventAppDoc(doctor,
                request.getLocale(), appUrl));


        return ResponseEntity.ok("It is successfully");
    }


}
