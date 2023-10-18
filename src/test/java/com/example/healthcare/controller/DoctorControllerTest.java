package com.example.healthcare.controller;

import com.example.healthcare.HealthcareApplication;
import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.dto.customer.CustomerResponse;
import com.example.healthcare.dto.doctor.DoctorCreateRequest;
import com.example.healthcare.dto.doctor.DoctorResponse;
import com.example.healthcare.error.InvalidObjectException;
import com.example.healthcare.mapping.CustomerMapper;
import com.example.healthcare.mapping.DoctorMapper;
import com.example.healthcare.model.AvailableHours;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.model.Photo;
import com.example.healthcare.service.CustomerService;
import com.example.healthcare.service.DoctorService;
import com.example.healthcare.validation.ObjectValidator;
import com.example.healthcare.web.DoctorController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Tag("integration")
public class DoctorControllerTest {

    @MockBean
    private DoctorService doctorService;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DoctorMapper doctorMapper;
    @MockBean
    private ObjectValidator validator;

    @Test
    void shouldHavePaginationOnFetchAll() throws Exception {

        Page<Doctor> emptyPersonPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(0, 10), 0);
        when(doctorService.fetchAll(0, 10)).thenReturn(emptyPersonPage);

        // Mock the behavior of your service and mapper to return a non-null Page
        //when(customerService.fetchAll(1, 10)).thenReturn(oneCustomerPage);
        when(doctorService.fetchAll(0, 10)).thenReturn(emptyPersonPage);
        //when(doctorMapper.responseFromModelOne(customer)).thenReturn(customerResponse);

        // Perform the request and validate the response
        mockMvc.perform(
                        get("/healthcare/doctors")
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
        Doctor doctor = Doctor.builder()
                .username("Ivan")
                .id(UUID.randomUUID())
                .build();
        Photo photo = Photo.builder()
                .doctor(doctor)
                .build();

        // Create a sample CustomerResponse
        DoctorResponse doctorResponse = DoctorResponse.builder()
                .username("Ivan")
                .personPhotoId(photo.getId())
                .build();

        when(doctorService.findById(anyString())).thenReturn(doctor);
        when(doctorMapper.responseFromModelOne(doctor)).thenReturn(doctorResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/healthcare/doctors/{doctorId}", doctor.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Ivan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.personPhotoId").value(photo.getId()));

    }


    @Test
    public void testFindByHospitalName() throws Exception {

        List<Doctor> doctorList = new ArrayList<>();
        List<DoctorResponse> doctorResponseList = new ArrayList<>();

        Doctor doctor = Doctor.builder()
                .username("Ivan")
                .hospitalName("ST.Anna")
                .build();
        Doctor doctor1 = Doctor.builder()
                .username("Gosho")
                .hospitalName("ST.Anna")
                .build();

        doctorList.add(doctor);
        doctorList.add(doctor1);

        DoctorResponse doctorResponse = DoctorResponse.builder()
                .username("Ivan")
                .hospitalName("ST.Anna")
                .build();
        DoctorResponse doctorResponse1 = DoctorResponse.builder()
                .username("Gosho")
                .hospitalName("ST.Anna")
                .build();
        doctorResponseList.add(doctorResponse);
        doctorResponseList.add(doctorResponse1);

        when(doctorService.findByHospitalName(anyString())).thenReturn(doctorList);
        when(doctorMapper.responseFromModelList(doctorList)).thenReturn(doctorResponseList);

        mockMvc.perform(MockMvcRequestBuilders.get("/healthcare/doctors/hospital/{hospitalName}", "ST.Anna"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("Ivan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value("Gosho"));


    }

    @Test
    public void testFindBySpecialty1() throws Exception {

        List<Doctor> doctorList = new ArrayList<>();
        List<DoctorResponse> doctorResponseList = new ArrayList<>();

        Doctor doctor = Doctor.builder()
                .username("Ivan")
                .specialty("surgeon")
                .build();
        Doctor doctor1 = Doctor.builder()
                .username("Gosho")
                .specialty("surgeon")
                .build();

        doctorList.add(doctor);
        doctorList.add(doctor1);

        DoctorResponse doctorResponse = DoctorResponse.builder()
                .username("Ivan")
                .specialty("surgeon")
                .build();
        DoctorResponse doctorResponse1 = DoctorResponse.builder()
                .username("Gosho")
                .specialty("surgeon")
                .build();
        doctorResponseList.add(doctorResponse);
        doctorResponseList.add(doctorResponse1);

        when(doctorService.findBySpecialty(anyString())).thenReturn(doctorList);
        when(doctorMapper.responseFromModelList(doctorList)).thenReturn(doctorResponseList);

        mockMvc.perform(MockMvcRequestBuilders.get("/healthcare/doctors/specialty/{specialty}", "surgeon"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("Ivan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value("Gosho"));


    }

    @Test
    public void testFindCatalog1() throws Exception {

        List<String> hours = new ArrayList<>();
        hours.add("11:00");
        hours.add("12:00");
        List<String> hours1 = new ArrayList<>();
        hours.add("11:00");
        hours.add("12:00");
        List<AvailableHours> list = new ArrayList<>();
        List<AvailableHoursDto> list1 = new ArrayList<>();

        AvailableHours availableHours1 = AvailableHours.builder()
                .firstName("Ivan")
                .hours(hours)
                .build();

        AvailableHours availableHours = AvailableHours.builder()
                .firstName("Ivan")
                .hours(hours)
                .build();
        list.add(availableHours);
        list.add(availableHours);
        AvailableHoursDto availableHoursDto = AvailableHoursDto.builder()
                .firstName("Ivan")
                .hours(hours)
                .build();
        AvailableHoursDto availableHoursDto1 = AvailableHoursDto.builder()
                .firstName("Ivan")
                .hours(hours)
                .build();
        list1.add(availableHoursDto);
        list1.add(availableHoursDto1);
        Doctor doctor = Doctor.builder()
                .username("Ivan")
                .specialty("surgeon")
                .availableHours(list)
                .build();
        when(doctorService.findByName(anyString())).thenReturn(doctor);
        when(doctorService.setAvailableHours(doctor)).thenReturn(list1);

        mockMvc.perform(MockMvcRequestBuilders.get("/healthcare/doctors/catalogHours/{doctorUserName}", "Ivan"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$[*].firstName").value(hasItems("Ivan", "Ivan")))
                .andExpect(jsonPath("$[0].hours").value(hasItems("11:00", "12:00")));


    }

    @Test
    public void testDeleteById() throws Exception {
        Doctor doctor = Doctor.builder()
                .id(UUID.randomUUID())
                .username("john_doe")
                .build();

        doNothing().when(doctorService).deleteById(anyString());

        mockMvc.perform(MockMvcRequestBuilders.delete("/healthcare/doctors/{doctorId}", doctor.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string("It is deleted!"));


        verify(doctorService, times(1)).deleteById(doctor.getId().toString());
        //Customer nonCustomer = customerService.findById((customer.getId().toString()));

        //assertNull(nonCustomer);


    }
    @Test
    public void testDeleteByEmail() throws Exception {
        Doctor doctor = Doctor.builder()
                .username("john_doe")
                .email("nikinikolov2002@gmail.com")
                .build();

        doNothing().when(doctorService).deleteByEmail(anyString());

        mockMvc.perform(MockMvcRequestBuilders.delete("/healthcare/doctors/email/{email}","nikinikolov2002@gmail.com"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string("It is deleted!"));


        verify(doctorService, times(1)).deleteByEmail(doctor.getEmail());
        //Customer nonCustomer = customerService.findById((customer.getId().toString()));

        //assertNull(nonCustomer);


    }
    @PostMapping("/registration")
    public ResponseEntity<String> createUserAndRegister(
            @RequestBody @Valid DoctorCreateRequest doctorDto,
            HttpServletRequest request, Errors errors)  {
        Map<String, String> validationErrors = validator.validate(doctorDto);
        if (validationErrors.size() != 0) {
            throw new InvalidObjectException("Invalid Doctor Create", validationErrors);
        }

        Doctor create = doctorMapper.modelFromCreateRequest(doctorDto);
        List<AvailableHours>doctorHours = create.getAvailableHours();
        String connect = doctorService.connectHours(create,doctorHours,request);


        return  ResponseEntity.ok().body(connect);

    }
    @Test
    public void testCreateUserAndRegister() throws Exception {

       Doctor doctor =Doctor.builder()
               .username("Ivan")
               .email("nikinikolov2002@gmail.com")
               .build();

       DoctorCreateRequest doctorDto =DoctorCreateRequest.builder()
               .username("Ivan")
               .email("nikinikolov2002@gmail.com")
               .build();
        Map<String, String> validationErrors = new HashMap<>();
        when(
                validator.validate(any())
        ).thenReturn(validationErrors);
        when(doctorMapper.modelFromCreateRequest(doctorDto)).thenReturn(doctor);
        when(doctorService.save(doctor)).thenReturn(doctor);





    }
    @Test
    public void testFindBySpecialty() throws Exception {

        mockMvc.perform(
                        get("/healthcare/doctors/specialty/infection-doctor" )
                                .contentType(MediaType.APPLICATION_JSON)

                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("Ivan2024"));


    }
    @Test
    public void testFindById1() throws Exception {

        // Create a sample Customer
        mockMvc.perform(
                        get("/healthcare/doctors/ad613e88-080d-43ca-b9ee-c52389b55c88" )
                                .contentType(MediaType.APPLICATION_JSON)

                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Ivan102"));


    }

    @Test
    @Transactional
    public void testCreateUserAndRegister1() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/healthcare/doctors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""

      {
              "username": "Ivan2026",
                     "password": "fefefe",
                     "firstName": "Stamat2026",
                     "lastName": "Stanatov2026",
                     "email": "nikinikolov2002@gmail.com",
                     "phoneNumber": "08842705050",
                     "specialty": "infection-doctor",
                     "hospitalName": "ST. Anna",
                     "available": true,
                     "availableHours": [
                         {
                             "firstName": "Stamat1",
                             "lastName": "Stanatov1",
                             "date": "2023-09-22",
                             "hours": [
                                 "09:00 AM",
                                 "10:00 AM",
                                 "11:00 AM"
                             ]
                         },
                         {
                             "firstName": "Stamat1",
                             "lastName": "Stanatov1",
                             "date": "2023-09-23",
                             "hours": [
                                 "09:30 AM",
                                 "10:30 AM"
                             ]
                         },
                         {
                             "firstName": "Stamat1",
                             "lastName": "Stanatov1",
                             "date": "2023-09-24",
                             "hours": []
                         }
                     ]
                 }
             
         
     """)).andExpect(status().isCreated())
                .andExpect(content().string("Registration Successfully!"));





    }
    @Test
    public void testCreateUserAndRegister11() throws Exception {
        // Define a DoctorCreateRequest object to send in the request body
        String requestBody = """
    {
        "username": "Ivan2026",
        "password": "fefefe",
        "firstName": "Stamat2026",
        "lastName": "Stanatov2026",
        "email": "nikinikolov2002@gmail.com",
        "phoneNumber": "08842705050"
    }
    """;

        // Perform the POST request
        mockMvc.perform(post("/healthcare/doctors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().string("Registration Successfully!"));
    }






}
