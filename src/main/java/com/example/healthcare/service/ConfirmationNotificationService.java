package com.example.healthcare.service;


import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
public class ConfirmationNotificationService {
    @Autowired
    private AppointmentRepository appointmentRepo;
    @Autowired
    private EmailService emailService;

    @Transactional
    @Scheduled(cron = "0 59 14 * * *")
    public void sendNotifications() {
        LocalDate currentDate = LocalDate.now();
        List<Appointment> appointmentsWithValidStartTime = appointmentRepo.findAppointmentsWithValidStartDate();

        for (Appointment appointment1 : appointmentsWithValidStartTime) {
            if (appointment1.getDoctor() != null) {
                LocalDate warningDateTime = appointment1.getStartDate().minusDays(1);
                List<Appointment> appointmentsToWarn = appointmentRepo.findAppointmentsToWarn(currentDate, warningDateTime);

                for (Appointment appointmentToWarn : appointmentsToWarn) {
                    Customer customer = appointmentToWarn.getCustomer();
                    Doctor doctor = appointmentToWarn.getDoctor();
                    String confirmationUrl = "http://localhost:8083/healthcare/appointments/customer/" + customer.getUsername();
                    String message = "Hello " + customer.getFirstName() + "\n Don't forget about the appointment with Dr. "
                            + doctor.getLastName()
                            + " at " + appointmentToWarn.getStartTime()
                            + "\n More information about the appointment here: "
                            + confirmationUrl + "\n\n See you soon!" + "\n\n\n Best regards," + "\n Healthcare";

                    emailService.sendSimpleMessage(customer.getEmail(), "Reminder Notification", message);
                }
            }
        }
    }
}