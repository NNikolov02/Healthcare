package com.example.healthcare.registration.appointment;

import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.registration.doctor.OnRegistrationCompleteEventDoctor;
import com.example.healthcare.service.AppointmentService;
import com.example.healthcare.service.DoctorService;
import com.example.healthcare.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AppointmentRegistrationListener implements ApplicationListener<OnRegistrationCompleteEventApp> {

    private final AppointmentService service;
    private final EmailService emailService;


    @Autowired
    public AppointmentRegistrationListener(AppointmentService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;

    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEventApp event) {
        this.confirmRegistration1(event);

    }

    private void confirmRegistration1(OnRegistrationCompleteEventApp event) {
        Appointment appointment = event.getAppointment();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(appointment, token);
        Customer customer = appointment.getCustomer();

        String recipientAddress = customer.getEmail();
        String subject = "Creating Appointment Confirmation";
        String confirmationUrl = event.getAppUrl() + "/confirmRegistration?token=" + token;
        String message = "Hello:"+customer.getUsername() + "\n" +"Thank you for reserving. This is the important appointment data that you will need :\n"+ "createDate:"
                + appointment.getCreateTime() +"\n" + "StartTime" + appointment.getStartTime() + "\n" + "EndTime:" + appointment.getEndTime() +
                "\n" + "Doctor:";

        emailService.sendSimpleMessage(recipientAddress, subject, message);
    }

}

