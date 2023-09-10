package com.example.healthcare.registration.customer;

import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.service.CustomerService;
import com.example.healthcare.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerDoctorAcceptListener implements ApplicationListener<OnDoctorCompleteEventCustomerAccept> {

    @Autowired
    private DoctorRepository doctorRepo;

    private final CustomerService service;
    private final EmailService emailService;


    @Autowired
    public CustomerDoctorAcceptListener(CustomerService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;

    }

    @Override
    public void onApplicationEvent(OnDoctorCompleteEventCustomerAccept event) {
        this.confirmRegistration(event);

    }

    private void confirmRegistration(OnDoctorCompleteEventCustomerAccept event) {
        Customer customer = event.getCustomer();
        String name = customer.getUsername();
        Doctor doctor = doctorRepo.findDoctorsByCustomerName(name);
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(customer, token);

        String recipientAddress = customer.getEmail();
        String subject = "Appointment Confirmation";
       // String confirmationUrl = event.getAppUrl() + "/confirmRegistration?token=" + token;
        String message = "Hello " + customer.getUsername() + "\n Dr." + doctor.getLastName() + " " + "accepted you appointment!"  ;

        emailService.sendSimpleMessage(recipientAddress, subject, message);
    }

}

