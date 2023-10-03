package com.example.healthcare.registration.appointment;

import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.service.AppointmentService;
import com.example.healthcare.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AppointmentDoctorListener implements ApplicationListener<OnRegistrationCompleteEventAppDoc> {

    private final AppointmentService service;
    private final EmailService emailService;


    @Autowired
    public AppointmentDoctorListener(AppointmentService service, EmailService emailService) {
        this.service = service;
        this.emailService = emailService;

    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEventAppDoc event) {
        this.confirmRegistration(event);

    }

    private void confirmRegistration(OnRegistrationCompleteEventAppDoc event) {
        Appointment appointment = event.getAppointment();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(appointment, token);
        Doctor doctor =appointment.getDoctor();
        Customer customer = appointment.getCustomer();

        String recipientAddress = doctor.getEmail();
        String subject = "Hello Dr. " + doctor.getLastName() + "\n" + "Would you accept this appointment?" ;
        String confirmationUrl = event.getAppUrl() + "/confirmRegistration?token=" + token;
        String message = "The patient " + customer.getLastName() + " wants to visit you at " + appointment.getStartTime()
                + "\n Would you accept? " + "\n Please click on the link to accept or decline this appointment:"
                + "\n http://localhost:8083/healthcare/doctors/accept/ " + appointment.getId();

        emailService.sendSimpleMessage(recipientAddress, subject, message);
    }

}
