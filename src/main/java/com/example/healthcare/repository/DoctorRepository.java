package com.example.healthcare.repository;

import com.example.healthcare.model.Doctor;
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
    Doctor deleteAllByEmail(String email);
    Doctor findByFirstNameAndLastName(String firstName,String lastName);
    @Query("SELECT DISTINCT d FROM Doctor d JOIN d.appointment a JOIN a.customer c WHERE c.username = :customerName")
    Doctor findDoctorsByCustomerName(@Param("customerName") String customerName);
    Doctor findByUsername(String userName);
}
