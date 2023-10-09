package com.example.healthcare.repository;

import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindByCustomersName() {
        List<Appointment> appointments = new ArrayList<>();
        Customer customer = Customer.builder()
                .username("Ivan")
                .lastName("Nikolov")
                .build();
        entityManager.persistAndFlush(customer);

        Appointment appointment = Appointment.builder()
                .reason("heart-problems")
                .customer(customer)
                .build();
        appointments.add(appointment);
        customer.setAppointments(appointments);
        entityManager.persistAndFlush(customer);
        entityManager.persistAndFlush(appointment);


        Appointment availableAppointment = appointmentRepo.findByCustomersName("Ivan");
        //assertThat(availableCustomers).contains(customer);


        assertEquals("heart-problems", availableAppointment.getReason());


    }
    @Test
    public void testDeleteByCustomerName() {
        List<Appointment> appointments = new ArrayList<>();
        Customer customer = Customer.builder()
                .username("Valentin")
                .lastName("Nikolov")
                .build();
        entityManager.persistAndFlush(customer);

        Appointment appointment = Appointment.builder()
                .reason("heart-problems")
                .customer(customer)
                .build();
        appointments.add(appointment);
        customer.setAppointments(appointments);
        entityManager.persistAndFlush(customer);
        entityManager.persistAndFlush(appointment);

        appointmentRepo.deleteAllAppointmentsByCustomerUsername("Valentin");
        Appointment nullAppointment = appointmentRepo.findByCustomersName("Valentin");

        assertNull(nullAppointment);
    }

    @Test
    public void testFindAppointmentsToWarn() {
        Appointment appointment = Appointment.builder()
                .startDate(LocalDate.of(2023, 10, 11))
                .build();
        entityManager.persistAndFlush(appointment);
        LocalDate warningDateTime = appointment.getStartDate().minusDays(1);

        List<Appointment> availableAppointments = appointmentRepo.findAppointmentsToWarn(LocalDate.now(), warningDateTime);

        assertThat(availableAppointments).contains(appointment);

    }
    @Test
    public void testFindAllByAvailable() {

        Appointment appointment = Appointment.builder()
                .reason("heart-problems")
                .build();
        entityManager.persistAndFlush(appointment);


        List<Appointment> availableAppointments = appointmentRepo.findAllById(appointment.getId());


        assertThat(availableAppointments).contains(appointment);
    }
    @Test
    public void testFindAppointmentsWithValidStartDate() {

        Appointment appointment = Appointment.builder()
                .reason("heart-problems")
                .startDate(LocalDate.of(2023, 10, 10))
                .build();
        entityManager.persistAndFlush(appointment);


        List<Appointment>appointments =appointmentRepo.findAppointmentsWithValidStartDate();




        assertThat(appointments).contains(appointment);
    }
}
