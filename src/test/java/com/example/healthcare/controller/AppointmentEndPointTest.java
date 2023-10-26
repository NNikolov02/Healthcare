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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test-h2")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class AppointmentEndPointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllAppointments() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("http://localhost:8083/healthcare/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("cdaf442f-253e-49c1-973e-e2cf064d50ea"))
                .andExpect(jsonPath("$.content[1].id").value("b0000a98-5f7f-4266-803c-69f38c68c05b"));
    }

    @Test
    public void testFindById() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("http://localhost:8083/healthcare/appointments/cdaf442f-253e-49c1-973e-e2cf064d50ea")
                                .contentType(MediaType.APPLICATION_JSON)


                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createTime").value("2023-10-19"))
                .andExpect(jsonPath("$.reason").value("heart-desiease"))
                .andExpect(jsonPath("$.url").value("http://localhost:8083/healthcare/appointments/cdaf442f-253e-49c1-973e-e2cf064d50ea"));



    }
    @Test
    public void testFindByCustomer() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("http://localhost:8083/healthcare/appointments/customer/NNikolov")
                                .contentType(MediaType.APPLICATION_JSON)


                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createTime").value("2023-10-19"))
                .andExpect(jsonPath("$.reason").value("heart-desiease"))
                .andExpect(jsonPath("$.url").value("http://localhost:8083/healthcare/appointments/cdaf442f-253e-49c1-973e-e2cf064d50ea"));



    }
    @Test
    public void testDeleteByCustomer() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        delete("http://localhost:8083/healthcare/appointments/Ivanov02")
                                .contentType(MediaType.APPLICATION_JSON)


                )
                .andExpect(status().isOk())
                .andExpect(content().string("It is deleted!"));


    }
    @Test
    public void testCreateAppointment() throws Exception {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Perform an HTTP POST request to your endpoint
        mockMvc.perform(MockMvcRequestBuilders.post("/healthcare/appointments/create/NNikolov")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
 {
 "reason": "random1"
            }
"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reason").value("random1"))
                .andExpect(jsonPath("$.customer.firstName").value("Radolsav"))
                .andExpect(jsonPath("$.customer.lastName").value("Spasov"))
                .andExpect(jsonPath("$.createTime").value(LocalDate.now().format(dateFormatter)));

        }

    @Test
    public void testUpdateAppointment() throws Exception {

        // Perform an HTTP POST request to your endpoint
        mockMvc.perform(MockMvcRequestBuilders.patch("/healthcare/appointments/cdaf442f-253e-49c1-973e-e2cf064d50ea")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
 {
 
            "reason": "heart-problem"
            
            }
"""))
                .andExpect(status().is(203))
                .andExpect(jsonPath("$.reason").value("heart-problem"))
                .andExpect(jsonPath("$.customer.firstName").value("Radolsav"))
                .andExpect(jsonPath("$.customer.lastName").value("Spasov"))
                .andExpect(jsonPath("$.createTime").value("2023-10-19"));



    }
    @Test
    public void testChooseDoctor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/healthcare/appointments/20693f15-d052-4886-a720-1149786e8ad0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
 {
 
                 "setFistName":"Stamat80",
                 "setLastName":"Stanatov80",
                 "setDate":"2023-09-22",
                 "setTime":  "11:15 AM"
            
            }
"""))
                .andExpect(status().isOk())
                .andExpect(content().string("The doctor is busy at that time or not found!"));



        // Perform an HTTP POST request to your endpoint
        mockMvc.perform(MockMvcRequestBuilders.put("/healthcare/appointments/20693f15-d052-4886-a720-1149786e8ad0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
 {
 
                 "setFistName":"Stamat80",
                 "setLastName":"Stanatov80",
                 "setDate":"2023-09-22",
                 "setTime":  "09:00 AM"
            
            }
"""))
                .andExpect(status().isOk())
                .andExpect(content().string("It is successfully"));
        }

}
