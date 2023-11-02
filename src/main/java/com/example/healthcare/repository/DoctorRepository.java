package com.example.healthcare.repository;

import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.model.Photo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorRepository extends CrudRepository<Doctor, UUID> {

    List<Doctor>findAllByAvailable(boolean available);
    List<Doctor>findAllByHospitalName(String name);
    List<Doctor>findAllBySpecialty(String specialty);
    Doctor findAllByEmail(String email);
    void deleteByEmail(String email);
    Doctor findByFirstNameAndLastName(String firstName,String lastName);

    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.appointments a JOIN a.customer c WHERE c.username = :customerName")
    Doctor findDoctorsByCustomerName(@Param("customerName") String customerName);
    Doctor findByUsername(String userName);
    Doctor findByLastName(String lastName);
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.appointments a WHERE a.id= :appointmentId")
    Doctor findByAppointmentId(@Param("appointmentId")UUID appointmentId);
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.appointments a JOIN a.customer c WHERE c.id = :id")
    Doctor findDoctorsByCustomerId(@Param("id") UUID customerName);
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.appointments a JOIN a.customer c WHERE c.id = :id")
    List<Doctor> findDoctorsByCustomerIdList(@Param("id") UUID customerName);
    @Query("SELECT DISTINCT p FROM Photo p JOIN p.doctor d WHERE d.username= :username")
    Photo findPhotoByDoctorUsername(@Param("username") String username);
}
