package com.example.healthcare.repository;

import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindAllByAvailable() {

        Customer customer = Customer.builder()
                .username("Ivan")
                .build();


        entityManager.persistAndFlush(customer);


        Customer availableCustomer = customerRepo.findCustomerByUsername("Ivan");


        assertEquals("Ivan",availableCustomer.getUsername());
    }
    @Test
    public void testDeleteCustomerByUsername() {

        Customer customer = Customer.builder()
                .username("Ivan")
                .build();


        entityManager.persistAndFlush(customer);


        customerRepo.deleteCustomerByUsername("Ivan");
        Customer nullCustomer = customerRepo.findCustomerByUsername("Ivan");


        assertNull(nullCustomer);
    }
    @Test
    public void testFindByAppointmentId(){
        List <Appointment> appointments = new ArrayList<>();
        for(Appointment appointment:appointments) {
            appointment = Appointment.builder().reason("ddd").build();
            entityManager.persistAndFlush(appointment);
        }

        Customer customer = Customer.builder()
                .username("Ivan")
                .lastName("Nikolov")
                .appointments(appointments)
                .build();

        entityManager.persistAndFlush(customer);
        for(Appointment appointment:appointments) {
            appointment.setCustomer(customer);
            entityManager.persistAndFlush(appointment);
            List<Customer> availableCustomers = customerRepo.findByAppointmentId(appointment.getId());
            assertThat(availableCustomers).contains(customer);
            for(Customer customer1:availableCustomers)

                assertEquals("Ivan",customer1.getUsername());
        }


    }


}
