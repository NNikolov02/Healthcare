package com.example.healthcare.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test-h2")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class DoctorEndPointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllDoctors() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("http://localhost:8083/healthcare/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("Ivan2001"))
                .andExpect(jsonPath("$.content[1].username").value("Ivan2002"));
    }

    @Test
    public void testFindById1() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("http://localhost:8083/healthcare/doctors/85cf83cf-7a3c-48fc-8b32-3d63766a265c" )
                                .contentType(MediaType.APPLICATION_JSON)



                )
                .andExpect(status().isOk())
                .andExpect( jsonPath("$.username").value("Ivan2002"));


    }
    @Test
    public void testFindBySpecialty() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("http://localhost:8083/healthcare/doctors/specialty/heart-sergeon" )
                                .contentType(MediaType.APPLICATION_JSON)



                )
                .andExpect(status().isOk())
                .andExpect( jsonPath("$[0].username").value("Ivan2001"))
                .andExpect( jsonPath("$[1].username").value("Ivan2002"));



    }
    @Test
    public void testFindByHospitalName() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("http://localhost:8083/healthcare/doctors/hospital/ST. Anna")
                                .contentType(MediaType.APPLICATION_JSON)


                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Ivan2001"))
                .andExpect(jsonPath("$[1].username").value("Ivan2002"));


    }
    @Test
    public void testFindByCatalogHours() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("http://localhost:8083/healthcare/doctors/catalogHours/Ivan2001")
                                .contentType(MediaType.APPLICATION_JSON)


                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Stamat80"))
                .andExpect(jsonPath("$[0].lastName").value("Stanatov80"))
                .andExpect(jsonPath("$[0].date").value("2023-09-22"))
                .andExpect(jsonPath("$[0].hours", contains("09:00 AM", "10:00 AM")))
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
                        delete("http://localhost:8083/healthcare/doctors/ad59c42b-0e86-440f-98c9-394cb48ec0ab")
                                .contentType(MediaType.APPLICATION_JSON)


                )
                .andExpect(status().isOk())
                .andExpect(content().string("It is deleted!"));


    }
    @Test
    public void testDeleteByEmail() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        delete("http://localhost:8083/healthcare/doctors/email/nikolov.agency@gmail.com")
                                .contentType(MediaType.APPLICATION_JSON)


                )
                .andExpect(status().isOk())
                .andExpect(content().string("It is deleted!"));


    }
    @Test
    public void testRegisterDoctor() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        post("/healthcare/doctors/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                            {
                                                                            
                                                                   "username": "Ivan2000",
                                                                             "password": "fefefe",
                                                                             "firstName": "Stamat90",
                                                                             "lastName": "Stanatov90",
                                                                             "email": "nikinikolov2002@gmail.com",
                                                                             "phoneNumber": "08842705050",
                                                                             "specialty": "heart-sergeon",
                                                                             "hospitalName": "ST. Anna",
                                                                             "available": false,
                                                                             "personPhotoId": null,
                                                                             "availableHours": [
                                                                                 {
                                                                                     "firstName": "Stamat80",
                                                                                     "lastName": "Stanatov80",
                                                                                     "date": "2023-09-22",
                                                                                     "hours": [
                                                                                         "09:00 AM",
                                                                                         "10:00 AM"
                                                                                     ]
                                                                                 },
                                                                                 {
                                                                                     "firstName": "Stamat80",
                                                                                     "lastName": "Stanatov80",
                                                                                     "date": "2023-09-23",
                                                                                     "hours": [
                                                                                         "09:30 AM",
                                                                                         "10:30 AM"
                                                                                     ]
                                                                                 },
                                                                                 {
                                                                                     "firstName": "Stamat80",
                                                                                     "lastName": "Stanatov80",
                                                                                     "date": "2023-09-24",
                                                                                     "hours": [
                                                                                         "11:15 AM"
                                                                                     ]
                                                                                 }
                                                                             ]
                                                                         }
                                        """)


                )
                .andExpect(status().isCreated())
                .andExpect(content().string("Registration Successfully!"));


    }
    @Test
    public void testAcceptApp() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        post("http://localhost:8083/healthcare/doctors/appointments/cdaf442f-253e-49c1-973e-e2cf064d50ea?setAccept=true")
                                .contentType(MediaType.APPLICATION_JSON)


                )

                .andExpect(status().isOk())
                .andExpect(content().string("The appointment is accepted"));
        mockMvc.perform(
                        post("http://localhost:8083/healthcare/doctors/appointments/cdaf442f-253e-49c1-973e-e2cf064d50ea?setAccept=false")
                                .contentType(MediaType.APPLICATION_JSON)


                )

                .andExpect(status().isOk())
                .andExpect(content().string("The appointment is not accepted"));



    }


}
