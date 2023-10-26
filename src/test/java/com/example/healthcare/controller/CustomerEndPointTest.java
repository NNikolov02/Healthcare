package com.example.healthcare.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ActiveProfiles("test-h2")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class CustomerEndPointTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testGetAllCustomers() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("http://localhost:8083/healthcare/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("NNikolov"))
                .andExpect(jsonPath("$.content[1].username").value("NNikolov02"));
    }

    @Test
    public void testFindById() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("http://localhost:8083/healthcare/customers/84918e71-6cbe-4987-b80c-07c64b751831")
                                .contentType(MediaType.APPLICATION_JSON)


                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("NNikolov"));


    }

    @Test
    public void testFindByName() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("http://localhost:8083/healthcare/customers/name/NNikolov")
                                .contentType(MediaType.APPLICATION_JSON)


                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("NNikolov"))
                .andExpect(jsonPath("$.url").value("http://localhost:8086/healthcare/customers/NNikolov"));


    }

    @Test
    public void testFindByCatalogHours() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("http://localhost:8083/healthcare/customers/catalogHours/Stanatov80")
                                .contentType(MediaType.APPLICATION_JSON)


                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Stamat80"))
                .andExpect(jsonPath("$[0].lastName").value("Stanatov80"))
                .andExpect(jsonPath("$[0].date").value("2023-09-22"))
                .andExpect(jsonPath("$[0].hours", contains("09:00 AM")))
                .andExpect(jsonPath("$[1].firstName").value("Stamat80"))
                .andExpect(jsonPath("$[1].lastName").value("Stanatov80"))
                .andExpect(jsonPath("$[1].date").value("2023-09-23"))
                .andExpect(jsonPath("$[1].hours", contains("09:30 AM", "10:30 AM")))
                .andExpect(jsonPath("$[2].firstName").value("Stamat80"))
                .andExpect(jsonPath("$[2].lastName").value("Stanatov80"))
                .andExpect(jsonPath("$[2].date").value("2023-09-24"))
                .andExpect(jsonPath("$[2].hours", contains("11:15 AM")));


    }
    @Test
    public void testDeleteById() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        delete("http://localhost:8083/healthcare/customers/22333f0b-e574-4b21-90a2-0090291f3a6f")
                                .contentType(MediaType.APPLICATION_JSON)


                )
                .andExpect(status().isOk())
                .andExpect(content().string("It is deleted!"));


    }
    @Test
    public void testDeleteByName() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        delete("http://localhost:8083/healthcare/customers/name/NNikolov03")
                                .contentType(MediaType.APPLICATION_JSON)


                )
                .andExpect(status().isOk())
                .andExpect(content().string("It is deleted!"));


    }
    @Test
    public void testCreateUserAndRegister() throws Exception {

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
    public void testUpdateUser() throws Exception {

        // Perform an HTTP POST request to your endpoint
        mockMvc.perform(MockMvcRequestBuilders.patch("/healthcare/customers/Ivanov02")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
 {
 
            "password": "nikola06"
            
            }
"""))
                .andExpect(status().is(200))
                .andExpect(content().string("It is updated successfully!"));



    }


}
