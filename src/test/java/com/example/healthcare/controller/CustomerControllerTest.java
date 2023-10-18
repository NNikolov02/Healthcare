package com.example.healthcare.controller;

import com.example.healthcare.HealthcareApplication;
import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.dto.customer.CustomerCreateRequest;
import com.example.healthcare.dto.customer.CustomerResponse;
import com.example.healthcare.dto.customer.CustomerUpdateRequest;
import com.example.healthcare.error.InvalidObjectException;
import com.example.healthcare.mapping.CustomerMapper;
import com.example.healthcare.model.AvailableHours;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.registration.customer.OnRegistrationCompleteEventCustomer;
import com.example.healthcare.service.CustomerService;
import com.example.healthcare.validation.ObjectValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;


import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = HealthcareApplication.class)
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @MockBean
    private CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomerMapper customerMapper;
    @MockBean
    private ObjectValidator validator;


    @Test
    void shouldHavePaginationOnFetchAll() throws Exception {
        // when service called fetchAll -> return empty result
        Customer customer = Customer.builder()
                .username("Ivan")
                .build();

        // Create a sample CustomerResponse
        CustomerResponse customerResponse = CustomerResponse.builder()
                .username("Ivan")
                .url("http://localhost:8086/healthcare/customers/Ivan")
                .build();

        // Create a page with one customer
       // Page<Customer> oneCustomerPage = new PageImpl<>(
               // Collections.singletonList(customer),
               // PageRequest.of(1, 10),
               // 1);
        Page<Customer> emptyPersonPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(0, 10) , 0);
        when(customerService.fetchAll(0, 10)).thenReturn(emptyPersonPage);

        // Mock the behavior of your service and mapper to return a non-null Page
        //when(customerService.fetchAll(1, 10)).thenReturn(oneCustomerPage);
        when(customerService.fetchAll(0, 10)).thenReturn(emptyPersonPage);
        when(customerMapper.responseFromModelOne(customer)).thenReturn(customerResponse);

        // Perform the request and validate the response
        mockMvc.perform(
                        get("/healthcare/customers")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.currentPage").value(0))
                .andExpect(jsonPath("$.pagination.pageSize").value(10))
                .andExpect(jsonPath("$.pagination.totalPages").value(0))
                .andExpect(jsonPath("$.pagination.totalElement").value(0));

        // You can also assert that the returned JSON contains customer information
       // mockMvc.perform(
                   //     get("/healthcare/customers")
                               // .contentType(MediaType.APPLICATION_JSON))
               // .andExpect(jsonPath("$.content[0].username").value("Ivan"))
                //.andExpect(jsonPath("$.content[0].url").value("http://localhost:8086/healthcare/customers/Ivan"));
    }


    @Test
    public void testFindById() throws Exception {
        // Create a sample Customer
        Customer customer = Customer.builder()
                .username("john_doe")
                .build();

        // Create a sample CustomerResponse
        CustomerResponse customerResponse = CustomerResponse.builder()
                .username("john_doe")
                .build();

        when(customerService.findById(anyString())).thenReturn(customer);
        when(customerMapper.responseFromModelOne(customer)).thenReturn(customerResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/healthcare/customers/{customerId}", UUID.randomUUID().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("john_doe"));

    }

    @Test
    public void testFindByName() throws Exception {
        Customer customer = Customer.builder()
                .username("john_doe")
                .build();

        // Create a sample CustomerResponse
        CustomerResponse customerResponse = CustomerResponse.builder()
                .username("john_doe")
                .build();

        // Mock the behavior of the repo.findById method to return the customer when called with any UUID
        when(customerService.findByUserName(anyString())).thenReturn(customer);
        when(customerMapper.responseFromModelOne(customer)).thenReturn(customerResponse);


        mockMvc.perform(MockMvcRequestBuilders.get("/healthcare/customers/name/{customerName}", "john_doe"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("john_doe"));
    }

    @Test
    public void testDeleteById() throws Exception {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .username("john_doe")
                .build();

        doNothing().when(customerService).deleteById(anyString());

        mockMvc.perform(MockMvcRequestBuilders.delete("/healthcare/customers/{customerId}", customer.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(customerService, times(1)).deleteById(customer.getId().toString());
        //Customer nonCustomer = customerService.findById((customer.getId().toString()));

        //assertNull(nonCustomer);


    }

    @Test
    public void testDeleteByName() throws Exception {
        Customer customer = Customer.builder()
                .username("john_doe")
                .build();

        doNothing().when(customerService).deleteByName(anyString());

        mockMvc.perform(MockMvcRequestBuilders.delete("/healthcare/customers/name/{customerName}", "john_doe"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(customerService, times(1)).deleteByName(customer.getUsername());
        //Customer nonCustomer = customerService.findById((customer.getId().toString()));

        //assertNull(nonCustomer);


    }

    @Test
    public void testCreateUserAndRegister() throws Exception {
        Customer customer = Customer.builder()
                .username("john_doe")
                .email("nikinikolov2002@gmail.com")
                .build();

        // Create a sample CustomerResponse
        CustomerCreateRequest customerDto = CustomerCreateRequest.builder()
                .username("john_doe")
                .email("nikinikolov2002@gmail.com")
                .build();

        Map<String, String> validationErrors = new HashMap<>();
        when(
                validator.validate(any())
        ).thenReturn(validationErrors);
        when(customerMapper.modelFromCreateRequest(customerDto)).thenReturn(customer);
        when(customerService.save(customer)).thenReturn(customer);
        String jsonRequest = "{\"username\":\"john_doe\", \"email\":\"nikinikolov2002@gmail.com\"}";  // Replace with your JSON as needed


        // Perform an HTTP POST request to your endpoint
        mockMvc.perform(MockMvcRequestBuilders.post("/healthcare/customers/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().string("Registration Successfully!"));


    }
    @Test
    public void testCreateUserAndRegister1() throws Exception {

        // Perform an HTTP POST request to your endpoint
        mockMvc.perform(MockMvcRequestBuilders.post("/healthcare/customers/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
 {
 "username": "Ivanov02",
            "password": "nikola02",
            "firstName": "Georgi",
            "lastName": "Ivanov02",
            "email": "nikinikolov2002@gmail.com",
            "phoneNumber": "0884175050",
            "dateOfBirth": "22.01.2002",
            "address": null
            }
"""))
                .andExpect(status().isCreated())
                .andExpect(content().string("Registration Successfully!"));


    }


    @Test
    public void testUpdateCustomer() throws Exception {
        Customer customer = Customer.builder()
                .username("Ivan")
                .email("nikinikolov2002@gmail.com")
                .build();

        // Create a sample CustomerUpdateRequest
        CustomerUpdateRequest customerDto = CustomerUpdateRequest.builder()
                .email("nikolaynikolov2002@gmail.com")
                .build();
        CustomerResponse customerResponse = CustomerResponse.builder()
                .username("Ivan")
                .email("nikolaynikolov2002@gmail.com")
                .build();

        Map<String, String> validationErrors = new HashMap<>();
        when(validator.validate(any())).thenReturn(validationErrors);
        when(customerService.findByUserName(anyString())).thenReturn(customer);
        customerMapper.updateModelFromDto(customerDto, customer);
        when(customerService.save(customer)).thenReturn(customer);
        when(customerMapper.responseFromModelOne(customer)).thenReturn(customerResponse);

        // Use double quotes and properly format the JSON content
        String jsonRequest = "{\"email\":\"nikolaynikolov2002@gmail.com\"}";

        mockMvc.perform(
                        patch("/healthcare/customers/{customerName}", "Ivan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest)
                )
                .andExpect(status().isNonAuthoritativeInformation()) // Use 203 status code
                .andExpect(jsonPath("$.email").value("nikolaynikolov2002@gmail.com"));
    }
    @Test
    public void testUpdateCustomer1() throws Exception {
        String jsonRequest = "{\"email\":\"nikolaynikolov2002@gmail.com\"}";
        mockMvc.perform(
                        patch("/healthcare/customers/{customerName}", "Ivan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                                .andExpect(status().isNonAuthoritativeInformation())// Use 203 status code
                .andExpect(jsonPath("$.email").value("nikolaynikolov2002@gmail.com"));



    }



}
