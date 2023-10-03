package com.example.healthcare.repository;

import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.Doctor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment, UUID> {
    @Query("SELECT a FROM Appointment a JOIN a.customer c WHERE c.username = :customerName")
    Appointment findByCustomersName(@Param("customerName") String customerName);
    @Query("SELECT a FROM Appointment a JOIN a.customer c WHERE c.username = :customerName")
    Appointment deleteByCustomerName(@Param("customerName") String customerName);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.firstName = :firstName AND a.doctor.lastName = :lastName")
    List<Appointment> findByDoctorFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    @Query("SELECT a FROM Appointment a WHERE a.startDate > :currentDate AND a.startDate <= :warningDate")
    List<Appointment> findAppointmentsToWarn(@Param("currentDate") LocalDate currentDate, @Param("warningDate") LocalDate warningDate);

    @Query("SELECT a FROM Appointment a WHERE a.startDate IS NOT NULL")
    List<Appointment> findAppointmentsWithValidStartDate();
    @Query("SELECT a FROM Appointment a WHERE a.startDate >= :currentDate")
    List<Appointment> findByDueDateAfter(LocalDate currentDate);
    List<Appointment> findAllById(UUID id);
}
