package com.example.healthcare.service;

import com.example.healthcare.dto.appointment.AppointmentCreateRequest;
import com.example.healthcare.dto.appointment.AppointmentResponse;
import com.example.healthcare.dto.appointment.AppointmentUpdateRequest;
import com.example.healthcare.dto.customer.CustomerUpdateRequest;
import com.example.healthcare.error.NotFoundObjectException;
import com.example.healthcare.mapping.AppointmentMapping;
import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.AvailableHours;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.registration.appointment.OnRegistrationCompleteEventApp;
import com.example.healthcare.registration.appointment.OnRegistrationCompleteEventAppDoc;
import com.example.healthcare.repository.AppointmentPagingRepository;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.CustomerRepository;
import com.example.healthcare.repository.DoctorRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Component
@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository repo;

    @Autowired
    private AppointmentPagingRepository pagingRepo;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private AppointmentMapping appointmentMapping;


    public Page<Appointment> fetchAll(int currentPage, int pageSize) {
        return pagingRepo.findAll(PageRequest.of(currentPage, pageSize));
    }

    public Appointment save(Appointment appointment){
        return repo.save(appointment);
    }
    public Appointment findById(String appointmentId) {
        return repo.findById(UUID.fromString(appointmentId)).orElse(null);

    }
    public Appointment findByCustomerName(String customerName){
        return repo.findByCustomersName(customerName);
    }

    public void deleteByName(String customerName){
        repo.deleteAllAppointmentsByCustomerUsername(customerName);
    }

    public String setAppointmentDoctor(Appointment appointment,Doctor doctor1, String firstName, String lastName, LocalDate date, String time, HttpServletRequest request) {

        if (appointment != null) {

            if (appointment.getDoctor() == null) {

                if (doctor1 != null) {
                    List<AvailableHours> availableHours = doctor1.getAvailableHours();
                    for (AvailableHours availableHours1 : availableHours) {
                        if (availableHours1.getDate().equals(date) && availableHours1.getHours().contains(time)) {
                            appointment.setStartDate(date);
                            appointment.setStartTime(time);
                            appointment.setDoctor(doctor1);
                            repo.save(appointment);

                        }
                    }
                    String appUrl1 = request.getContextPath();
                    eventPublisher.publishEvent(new OnRegistrationCompleteEventAppDoc(appointment, request.getLocale(), appUrl1));
                    return "It is successfully";
                }


            }
        }

        return "The doctor is busy at that time or not found!";
    }
    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
    public void createVerificationToken(Appointment appointment, String token) {
        System.out.println("Creating verification token for doctor: " + appointment.getId());
        System.out.println("Token: " + token);
    }

    public AppointmentResponse createApp(Customer existingCustomer, AppointmentCreateRequest appointmentDto,HttpServletRequest request) {

        if (existingCustomer != null) {

            Appointment create = appointmentMapping.modelFromCreateRequest(appointmentDto);
            create.setCreateTime(LocalDate.now());
            create.setCustomer(existingCustomer);
            //create.setEndTime(create.getStartTime().plusHours(1).plusMinutes(30));

            //existingCustomer.getCarts().add(create);

            Appointment saved = repo.save(create);

            AppointmentResponse cartResponse = appointmentMapping.responseFromModelOne(saved);


            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEventApp(saved,
                    request.getLocale(), appUrl));

            return cartResponse;


        }
        return null;
    }
    public String updateAppointment(Appointment appointment, AppointmentUpdateRequest appointmentDto){
        appointmentMapping.updateModelFromDto(appointmentDto,appointment);

        Appointment saved = repo.save(appointment);

        return "It is updated successfully!";


    }


}
