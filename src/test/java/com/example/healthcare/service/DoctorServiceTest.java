package com.example.healthcare.service;

import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.dto.doctor.DoctorCreateRequest;
import com.example.healthcare.mapping.DoctorMapper;
import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.AvailableHours;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.registration.customer.OnDoctorCompleteEventCustomerAccept;
import com.example.healthcare.registration.customer.OnDoctorCompleteEventCustomerDecline;
import com.example.healthcare.registration.doctor.OnRegistrationCompleteEventDoctor;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.DoctorPagingRepository;
import com.example.healthcare.repository.DoctorRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.print.Doc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNull;

@SpringBootTest
public class DoctorServiceTest {

    @MockBean
    private DoctorRepository repo;
    @Autowired
    private DoctorService doctorService;

    @MockBean
    private DoctorPagingRepository pagingRepo;
    @MockBean
    private ApplicationEventPublisher eventPublisher;
    @MockBean
    private AppointmentRepository appointmentRepo;
    @MockBean
    private DoctorMapper doctorMapper;
    @Mock
    private EmailService emailService;

//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//        doNothing().when(emailService).sendSimpleMessage(any(), any(), any());
//    }

    @Test
    public void testSaveDoctor() {
        Doctor doctor = Doctor.builder()
                .username("Ivan")
                .build();
        when(repo.save(doctor)).thenReturn(doctor);

        Doctor savedDoctor = doctorService.save(doctor);


        assertEquals("Doctors should be the same ",doctor, savedDoctor);
    }
    @Test
    public void testFetchAll() {
        List<Doctor> doctorList = new ArrayList<>();

        Page<Doctor> doctors = new PageImpl<>(doctorList);

        int currentPage = 1;
        int pageSize = 10;

        when(pagingRepo.findAll(PageRequest.of(currentPage, pageSize))).thenReturn(doctors);

        Page<Doctor> result = doctorService.fetchAll(currentPage, pageSize);


        assertEquals("See if it equal",doctors, result);

        verify(pagingRepo, times(1)).findAll(PageRequest.of(currentPage, pageSize));
    }
    @Test
    public void testFindDoctorById() {
        Doctor doctor = Doctor.builder()
                .id(UUID.randomUUID())
                .username("Ivan")
                .build();

        when(repo.findById(doctor.getId())).thenReturn(Optional.of(doctor));

        Doctor findDoctor = doctorService.findById(doctor.getId().toString());


        assertEquals("Doctors should be the same ",doctor, findDoctor);
    }
    @Test
    public void testFindDoctorByEmail() {
        Doctor doctor = Doctor.builder()
                .username("Ivan")
                .email("nikinikolov2002@gmail.com")
                .build();

        when(repo.findAllByEmail(doctor.getEmail())).thenReturn(doctor);

        Doctor findDoctor = doctorService.findByEmail(doctor.getEmail());


        assertEquals("Doctors should be the same ",doctor, findDoctor);
    }
    @Test
    public void testFindDoctorByName() {
        Doctor doctor = Doctor.builder()
                .username("Ivan")
                .build();



        when(repo.findByUsername(doctor.getUsername())).thenReturn(doctor);

        Doctor findDoctor = doctorService.findByName(doctor.getUsername());


        assertEquals("Doctors should be Ivan ",doctor, findDoctor);
    }
    @Test
    public void testFindDoctorsByHospitalName() {
        List<Doctor> doctors = new ArrayList<>();
        Doctor doctor = Doctor.builder()
                .username("Ivan")
                .hospitalName("Zdrave")
                .build();
        doctors.add(doctor);
        Doctor doctor1 = Doctor.builder()
                .username("Gosho")
                .hospitalName("Zdrave")
                .build();
        doctors.add(doctor1);


        when(repo.findAllByHospitalName(doctor.getHospitalName())).thenReturn(doctors);

        List<Doctor> findDoctors = doctorService.findByHospitalName("Zdrave");


        assertEquals("Doctors should be in Zdrave ",doctors, findDoctors);
    }
    @Test
    public void testFindDoctorsBySpecialty() {
        List<Doctor> doctors = new ArrayList<>();
        Doctor doctor = Doctor.builder()
                .username("Ivan")
                .specialty("surgeon")
                .build();
        doctors.add(doctor);
        Doctor doctor1 = Doctor.builder()
                .username("Gosho")
                .specialty("surgeon")
                .build();
        doctors.add(doctor1);


        when(repo.findAllBySpecialty("surgeon")).thenReturn(doctors);

        List<Doctor> findDoctors = doctorService.findBySpecialty("surgeon");


        assertEquals("Doctors should be surgeon ",doctors, findDoctors);
    }
    @Test
    public void testDeleteDoctorById() {

        Doctor doctor = Doctor.builder()
                .id(UUID.randomUUID())
                .username("Ivan")
                .specialty("surgeon")
                .build();


        doNothing().when(repo).deleteById(doctor.getId());


        doctorService.deleteById(doctor.getId().toString());


        verify(repo).deleteById(doctor.getId());


        Doctor deletedDoctor = doctorService.findById(doctor.getId().toString());
        assertNull("Doctor should be deleted", deletedDoctor);
    }
    @Test
    public void testDeleteDoctorByEmail() {

        Doctor doctor = Doctor.builder()
                .username("Ivan")
                .specialty("surgeon")
                .email("nikinikolov2002@gmail.com")
                .build();


        doNothing().when(repo).deleteByEmail(doctor.getEmail());


        doctorService.deleteByEmail(doctor.getEmail());


        verify(repo).deleteByEmail(doctor.getEmail());


        Doctor deletedDoctor = doctorService.findByEmail(doctor.getEmail());
        assertNull("Doctor should be deleted", deletedDoctor);
    }
    @Test
    public void testCreateDoctorAndConnectHours(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        List<String> hours = new ArrayList<>();
        hours.add("11:00");
        hours.add("12:00");
        List<AvailableHoursDto>list = new ArrayList<>();
        AvailableHoursDto  availableHoursDto = AvailableHoursDto.builder()
                .hours(hours)
                .build();
        AvailableHoursDto availableHoursDto1 = AvailableHoursDto.builder()
                .hours(hours)
                .build();
        list.add(availableHoursDto1);
        list.add(availableHoursDto);
        List<AvailableHours>list1 = new ArrayList<>();
        AvailableHours availableHours = AvailableHours.builder()
                .hours(hours)
                .build();
        AvailableHours availableHours1 = AvailableHours.builder()
                .hours(hours)
                .build();
        list1.add(availableHours);
        list1.add(availableHours1);

        DoctorCreateRequest doctorDto = DoctorCreateRequest.builder()
                .username("Ivan")
                .email("nikinikolov2002@gmail.com")
                .availableHours(list)
                .build();
        Doctor doctor =Doctor.builder()
                .username("Ivan")
                .email("nikinikolov2002@gmail.com")
                .availableHours(list1)
                .build();

        when(doctorMapper.modelFromCreateRequest(doctorDto)).thenReturn(doctor);
        when(repo.save(doctor)).thenReturn(doctor);

        String result = doctorService.connectHours(doctorDto, request);

        // Assertions
        assertEquals("If they are he same","Registration Successfully!", result);
        verify(repo, times(1)).save(doctor);
        //verify(eventPublisher, times(1)).publishEvent(any(OnRegistrationCompleteEventDoctor.class));



    }
    @Test
    public void testSetAvailableHours(){
        List<String> hours = new ArrayList<>();
        hours.add("11:00");
        hours.add("12:00");
        List<AvailableHoursDto>list = new ArrayList<>();
        AvailableHoursDto  availableHoursDto = AvailableHoursDto.builder()
                .hours(hours)
                .build();
        AvailableHoursDto availableHoursDto1 = AvailableHoursDto.builder()
                .hours(hours)
                .build();
        list.add(availableHoursDto1);
        list.add(availableHoursDto);
        List<AvailableHours>list1 = new ArrayList<>();
        AvailableHours availableHours = AvailableHours.builder()
                .hours(hours)
                .build();
        AvailableHours availableHours1 = AvailableHours.builder()
                .hours(hours)
                .build();
        list1.add(availableHours);
        list1.add(availableHours1);

        Doctor doctor =Doctor.builder()
                .username("Ivan")
                .email("nikinikolov2002@gmail.com")
                .availableHours(list1)
                .build();

        when(doctorMapper.responseFromModelHours(doctor.getAvailableHours())).thenReturn(list);

        List<AvailableHoursDto>doctorHours =doctorService.setAvailableHours(doctor);

        assertEquals("If they are he same",list, doctorHours);




    }
    @Test
    public void testSetApp(){
        List<String> hours = new ArrayList<>();
        hours.add("11:00");
        hours.add("12:00");
        List<AvailableHours>list1 = new ArrayList<>();
        AvailableHours availableHours = AvailableHours.builder()
                .hours(hours)
                .date(LocalDate.now())
                .build();
        AvailableHours availableHours1 = AvailableHours.builder()
                .hours(hours)
                .date(LocalDate.now().minusDays(1))
                .build();
        list1.add(availableHours);
        list1.add(availableHours1);
        HttpServletRequest request1 = mock(HttpServletRequest.class);
        Appointment appointment = Appointment.builder()
                .reason("heart-problems")
                .build();
        List<Appointment>appointments = new ArrayList<>();
        appointments.add(appointment);

        Customer customer = Customer.builder()
                .username("Ivan")
                .email("nikinikolov2002@gmail.com")
                .appointments(appointments)
                .build();
        Doctor doctor = Doctor.builder()
                .username("Gosho")
                .lastName("Ivanov")
                .email("nikinikolov2002@gmail.com")
                .appointments(appointments)
                .availableHours(list1)
                .build();
        boolean setAccept;


        String setAccT = doctorService.setApp(doctor,customer,appointments,true,request1);
        String setAccF = doctorService.setApp(doctor,customer,appointments,false,request1);



        assertEquals("If they are he same","The appointment is accepted", setAccT);
        assertEquals("If they are he same","The appointment is not accepted", setAccF);
        //String appUrl = request1.getContextPath();

        //verify(eventPublisher, times(1)).publishEvent(new OnDoctorCompleteEventCustomerDecline(customer, request1.getLocale(), appUrl));

    }



}
