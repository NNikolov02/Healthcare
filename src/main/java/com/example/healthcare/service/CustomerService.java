package com.example.healthcare.service;

import com.example.healthcare.error.NotFoundObjectException;
import com.example.healthcare.model.Customer;
import com.example.healthcare.repository.CustomerPagingRepository;
import com.example.healthcare.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Component
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repo;

    @Autowired
    private CustomerPagingRepository pagingRepo;

    public Page<Customer> fetchAll(int currentPage, int pageSize) {
        return pagingRepo.findAll(PageRequest.of(currentPage, pageSize));
    }

    public  Customer save(Customer customer){
        return repo.save(customer);
    }

    public Customer findById(String customerId) {
        return repo.findById(UUID.fromString(customerId)).orElseThrow(() -> {
            throw new NotFoundObjectException("Customer Not Found", Customer.class.getName(), customerId);
        });
    }
    public Customer findByUserName(String name){
        return repo.findCustomerByUsername(name);
    }

    public void deleteById(String customerId){
        repo.deleteById(UUID.fromString(customerId));
    }

    @Transactional
    public void deleteByName(String name){
        repo.deleteCustomerByUsername(name);
    }
    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
    public void createVerificationToken(Customer customer, String token) {
        System.out.println("Creating verification token for customer: " + customer.getUsername());
        System.out.println("Token: " + token);
    }
}