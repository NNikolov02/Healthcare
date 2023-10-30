package com.example.healthcare.repository;

import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, UUID> {

    Customer findCustomerByUsername(String name);
    void deleteCustomerByUsername(String name);

    @Query("SELECT DISTINCT c FROM Customer c JOIN c.appointments a WHERE a.doctor.username= :doctorUsername")
    List<Customer> findAllCustomersByDoctorUsername(@Param("doctorUsername") String doctorUsername);
    @Query("SELECT DISTINCT c FROM Customer c JOIN c.appointments a WHERE a.id= :appointmentId")
    Customer findByAppointmentId(@Param("appointmentId")UUID appointmentId);
}

