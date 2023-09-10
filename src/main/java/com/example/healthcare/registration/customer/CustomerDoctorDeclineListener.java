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
public class CustomerDoctorDeclineListener implements ApplicationListener<OnDoctorCompleteEventCustomerDecline> {

    @Autowired
    private DoctorRepository doctorRepo;

    private final CustomerService service;
    private final EmailService emailService;


    @Autowired
    public CustomerDoctorDeclineListener(CustomerService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;

    }

    @Override
    public void onApplicationEvent(OnDoctorCompleteEventCustomerDecline event) {
        this.confirmRegistration(event);

    }

    private void confirmRegistration(OnDoctorCompleteEventCustomerDecline event) {
        Customer customer = event.getCustomer();
        String name = customer.getUsername();
        Doctor doctor = doctorRepo.findDoctorsByCustomerName(name);
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(customer, token);

        String recipientAddress = customer.getEmail();
        String subject = "Appointment Confirmation";
        // String confirmationUrl = event.getAppUrl() + "/confirmRegistration?token=" + token;
        String message = "Hello " + customer.getUsername() + "\n Dr." + doctor.getLastName() + " " + "declined you appointment!" +
                "\n If you want to connect with the doctor directly " + "\n Contact: " + doctor.getEmail()  ;

        emailService.sendSimpleMessage(recipientAddress, subject, message);
    }

}

