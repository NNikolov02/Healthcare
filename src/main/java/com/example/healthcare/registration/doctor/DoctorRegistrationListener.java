package com.example.healthcare.registration.doctor;

import com.example.healthcare.model.Doctor;
import com.example.healthcare.service.DoctorService;
import com.example.healthcare.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DoctorRegistrationListener implements ApplicationListener<OnRegistrationCompleteEventDoctor> {

    private final DoctorService service;
    private final EmailService emailService;


    @Autowired
    public DoctorRegistrationListener(DoctorService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;

    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEventDoctor event) {
        this.confirmRegistration(event);

    }

    private void confirmRegistration(OnRegistrationCompleteEventDoctor event) {
        Doctor doctor = event.getDoctor();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(doctor, token);


        String recipientAddress = doctor.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = event.getAppUrl() + "/confirmRegistration?token=" + token;
        String message = "Thank you for registering. Please click on the link below to verify your email address:\n"
                + confirmationUrl;

        emailService.sendSimpleMessage(recipientAddress, subject, message);
    }

}

