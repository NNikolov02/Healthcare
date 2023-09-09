package com.example.healthcare.service;

import com.example.healthcare.error.NotFoundObjectException;
import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.repository.AppointmentPagingRepository;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.CustomerRepository;
import com.example.healthcare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Component
@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository repo;

    @Autowired
    private AppointmentPagingRepository pagingRepo;

    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private DoctorRepository doctorRepo;


    public Page<Appointment> fetchAll(int currentPage, int pageSize) {
        return pagingRepo.findAll(PageRequest.of(currentPage, pageSize));
    }

    public Appointment save(Appointment appointment){
        return repo.save(appointment);
    }
    public Appointment findById(String appointmentId) {
        return repo.findById(UUID.fromString(appointmentId)).orElseThrow(() -> {
            throw new NotFoundObjectException("Appointment Not Found", Appointment.class.getName(), appointmentId);
        });
    }
    public Appointment findByCustomerName(String customerName){
        return repo.findByCustomersName(customerName);
    }

    public void deleteByName(String customerName){
        repo.deleteByCustomerName(customerName);
    }
    public Appointment setAppointmentDoctor(String customerName, String firstName, String lastName) {
        Appointment appointment = repo.findByCustomersName(customerName);

        if (appointment != null) {
            // Check if the appointment already has a doctor
            if (appointment.getDoctor() == null) {
                Doctor doctor = doctorRepo.findByFirstNameAndLastName(firstName, lastName);
                if (doctor != null) {
                    appointment.setDoctor(doctor);
                    repo.save(appointment);


                    return appointment;
                }
            }
        }
        return null;
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
}
