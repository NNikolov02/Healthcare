package com.example.healthcare.service;

import com.example.healthcare.dto.customer.CustomerCreateRequest;
import com.example.healthcare.dto.customer.CustomerUpdateRequest;
import com.example.healthcare.mapping.CustomerMapper;
import com.example.healthcare.mapping.DoctorMapper;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.repository.CustomerPagingRepository;
import com.example.healthcare.repository.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNull;

@SpringBootTest
public class CustomerServiceTest {

    @MockBean
    private CustomerRepository repo;

    @MockBean
    private CustomerPagingRepository pagingRepo;
    @Autowired
    private CustomerService customerService;
    @MockBean
    private ApplicationEventPublisher eventPublisher;
    @MockBean
    private CustomerMapper customerMapper;
    @MockBean
    private DoctorMapper doctorMapper;

    @Test
    public void testSaveCustomer() {
        Customer customer = Customer.builder()
                .username("Ivan")
                .build();
        when(repo.save(customer)).thenReturn(customer);

        Customer savedCustomer = customerService.save(customer);


        assertEquals("Customers should be the same ",customer, savedCustomer);
    }
    @Test
    public void testFetchAll() {
        List<Customer> customerList = new ArrayList<>();

        Page<Customer> customers = new PageImpl<>(customerList);

        int currentPage = 1;
        int pageSize = 10;

        when(pagingRepo.findAll(PageRequest.of(currentPage, pageSize))).thenReturn(customers);

        Page<Customer> result = customerService.fetchAll(currentPage, pageSize);


        assertEquals("See if it equal",customers, result);

        verify(pagingRepo, times(1)).findAll(PageRequest.of(currentPage, pageSize));
    }
    @Test
    public void testFindCustomerById() {

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .username("Ivan")
                .build();

        when(repo.findById(customer.getId())).thenReturn(Optional.of(customer));

        Customer findCustomer = customerService.findById(customer.getId().toString());

        assertEquals("See if it equal",customer, findCustomer);


    }

    @Test
    public void testFindCustomerByName() {

        Customer customer = Customer.builder()
                .username("Ivan")
                .build();

        when(repo.findCustomerByUsername(customer.getUsername())).thenReturn(customer);

        Customer findCustomer = customerService.findByUserName(customer.getUsername());

        assertEquals("See if it equal",customer, findCustomer);


    }
    @Test
    public void testDeleteById(){
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .username("Ivan")
                .build();

        doNothing().when(repo).deleteById(customer.getId());

        customerService.deleteById(customer.getId().toString());

        verify(repo).deleteById(customer.getId());

        Customer nullCustomer = customerService.findById(customer.getId().toString());

       assertNull("See if is null",nullCustomer);
    }
    @Test
    public void testDeleteByName(){
        Customer customer = Customer.builder()
                .username("Ivan")
                .build();

        doNothing().when(repo).deleteCustomerByUsername(customer.getUsername());

        customerService.deleteByName(customer.getUsername());

        verify(repo).deleteCustomerByUsername(customer.getUsername());

        Customer nullCustomer = customerService.findByUserName(customer.getUsername());

        assertNull("See if is null",nullCustomer);
    }
    @Test
    public void testRegisterCustomer(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        Customer customer = Customer.builder()
                .username("Ivan")
                .email("nikinikolov2002@gmail.com")
                .build();
        CustomerCreateRequest customerDto = CustomerCreateRequest.builder()
                .username("Ivan")
                .email("nikinikolov2002@gmail.com")
                .build();

        when(customerMapper.modelFromCreateRequest(customerDto)).thenReturn(customer);
        when(repo.save(customer)).thenReturn(customer);

        String createCustomer = customerService.registerUser(customerDto,request);

        assertEquals("Create Customer","Registration Successfully!", createCustomer);
    }
    @Test
    public void testUpdateCustomer(){
        Customer customer = Customer.builder()
                .username("Ivan")
                .email("nikinikolov2002@gmail.com")
                .build();
        CustomerUpdateRequest customerDto = CustomerUpdateRequest.builder()
                .email("nikolaynikolov2002@gmail.com")
                .build();
//       doNothing().when(customerMapper).updateModelFromDto(customerDto,customer);
//       when(repo.save(customer)).thenReturn(customer);
        String updateCustomer = customerService.updateCustomer(customer,customerDto);

        assertEquals("Update Customer","It is updated successfully!", updateCustomer);


    }



}
