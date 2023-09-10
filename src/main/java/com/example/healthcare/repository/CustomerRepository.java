package com.example.healthcare.repository;

import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, UUID> {

    Customer findCustomerByUsername(String name);
    Customer deleteCustomerByUsername(String name);

    @Query("SELECT DISTINCT c FROM Customer c JOIN c.appointments a WHERE a.doctor.username= :doctorUsername")
    Customer findCustomersByDoctorUsername(@Param("doctorUsername") String doctorUsername);
}
