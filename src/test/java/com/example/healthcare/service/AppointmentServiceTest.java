package com.example.healthcare.service;

import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.dto.CustomerDto;
import com.example.healthcare.dto.appointment.AppointmentCreateRequest;
import com.example.healthcare.dto.appointment.AppointmentResponse;
import com.example.healthcare.dto.appointment.AppointmentUpdateRequest;
import com.example.healthcare.dto.customer.CustomerUpdateRequest;
import com.example.healthcare.dto.doctor.DoctorCreateRequest;
import com.example.healthcare.dto.doctor.SetDoctorRequest;
import com.example.healthcare.mapping.AppointmentMapping;
import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.AvailableHours;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.repository.AppointmentPagingRepository;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.CustomerRepository;
import com.example.healthcare.repository.DoctorRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNull;

@SpringBootTest
public class AppointmentServiceTest {

   @MockBean
    private AppointmentRepository repo;

    @MockBean
    private AppointmentPagingRepository pagingRepo;
    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private AppointmentService appointmentService;
    @MockBean
    private DoctorRepository doctorRepo;
    @MockBean
    private AppointmentMapping appointmentMapping;

    @Test
    public void testFetchAll() {
        List<Appointment> appointmentList = new ArrayList<>();

        Page<Appointment> appointments= new PageImpl<>(appointmentList);

        int currentPage = 1;
        int pageSize = 10;

        when(pagingRepo.findAll(PageRequest.of(currentPage, pageSize))).thenReturn(appointments);

        Page<Appointment> result = appointmentService.fetchAll(currentPage, pageSize);


        assertEquals("See if it equal",appointments, result);

        verify(pagingRepo, times(1)).findAll(PageRequest.of(currentPage, pageSize));
    }
    @Test
    public void testFindAppointmentByCustomerName(){

        Customer customer = Customer.builder()
                .username("Ivan")
                .build();
        Appointment appointment = Appointment.builder()
                .customer(customer)
                .build();

        when(repo.findByCustomersName(customer.getUsername())).thenReturn(appointment);

        Appointment findAppointment = appointmentService.findByCustomerName(customer.getUsername());

        assertEquals("If it is equal",appointment,findAppointment);

    }
    @Test
    public void testSaveAppointment(){
        Appointment appointment =Appointment.builder()
                .reason("heart-problems")
                .build();

        when(repo.save(appointment)).thenReturn(appointment);

        Appointment findAppointment = appointmentService.save(appointment);

        assertEquals("If it is equal",appointment,findAppointment);
    }
    @Test
    public void testFindAppointmentById(){

        Appointment appointment =Appointment.builder()
                .id(UUID.randomUUID())
                .reason("heart-problems")
                .build();
        when(repo.findById(appointment.getId())).thenReturn(Optional.of(appointment));

        Appointment findAppointment = appointmentService.findById(appointment.getId().toString());

        assertEquals("If it is equal",appointment,findAppointment);

    }
    @Test
    public void testDeleteAppointmentByCustomerName(){
        Customer customer = Customer.builder()
                .username("Ivan")
                .build();
        Appointment appointment = Appointment.builder()
                .reason("heart-problems")
                .customer(customer)
                .build();

        doNothing().when(repo).deleteAllAppointmentsByCustomerUsername(customer.getUsername());


        appointmentService.deleteByName(customer.getUsername());
        verify(repo).deleteAllAppointmentsByCustomerUsername(customer.getUsername());

        Appointment nullAppointment = appointmentService.findByCustomerName(customer.getUsername());

        assertNull("There is not appointment",nullAppointment);

    }
    @Test
    public void createAppointment(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        Customer customer = Customer.builder()
                .username("Ivan")
                .firstName("Gosho")
                .email("nikinikolov2002@gmail.com")
                .build();
        CustomerDto customerDto = CustomerDto.builder()
                .firstName("Gosho")
                .email("nikinikolov2002@gmail.com")
                .build();
        Appointment appointment = Appointment.builder()
                .reason("heart-problems")
                .customer(customer)
                .createTime(LocalDate.now())
                .build();
        AppointmentCreateRequest appointmentDto = AppointmentCreateRequest.builder()
                .reason("heart-problems")
                .build();
        AppointmentResponse appointmentResponse = AppointmentResponse.builder()
                .reason("heart-problems")
                .customer(customerDto)
                .createTime(LocalDate.now())
                .build();

        when(appointmentMapping.modelFromCreateRequest(appointmentDto)).thenReturn(appointment);
        when(appointmentService.save(appointment)).thenReturn(appointment);
        when(appointmentMapping.responseFromModelOne(appointment)).thenReturn(appointmentResponse);

        AppointmentResponse createAppointmentResponse = appointmentService.createApp(customer,appointmentDto,request);

        assertEquals("If is equal",appointmentResponse,createAppointmentResponse);


    }
    @Test
    public void testChooseDoctor(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        Customer customer = Customer.builder()
                .username("Ivan")
                .lastName("Ivanov")
                .email("nikinikolov2002@gmail.com")
                .build();
        List<String> hours = new ArrayList<>();
        hours.add("11:00");
        hours.add("12:00");;
        List<AvailableHours>list1 = new ArrayList<>();
        AvailableHours availableHours = AvailableHours.builder()
                .date(LocalDate.now())
                .hours(hours)
                .build();
        AvailableHours availableHours1 = AvailableHours.builder()
                .date(LocalDate.now().minusDays(1))
                .hours(hours)
                .build();
        list1.add(availableHours);
        list1.add(availableHours1);
        List<Appointment>appointments = new ArrayList<>();
        Appointment appointment = Appointment.builder()
                .id(UUID.randomUUID())
                .reason("heart-problem")
                .customer(customer)
                .build();
        appointments.add(appointment);
        Doctor doctor =Doctor.builder()
                .username("Ivan")
                .firstName("Gosho")
                .lastName("Ivanov")
                .email("nikinikolov2002@gmail.com")
                .availableHours(list1)
                .appointments(appointments)
                .build();
        SetDoctorRequest doctorDto = SetDoctorRequest.builder()
                .setFistName("Gosho")
                .setLastName("Ivanov")
                .setDate(LocalDate.now())
                .setTime("11:00")
                .build();
        SetDoctorRequest doctorDto1 = SetDoctorRequest.builder()
                .setFistName("Gosho")
                .setLastName("Ivanov")
                .setDate(LocalDate.now())
                .setTime("14:00")
                .build();

        when(doctorRepo.findByAppointmentId(appointment.getId())).thenReturn(doctor);
        when(repo.save(appointment)).thenReturn(appointment);

        String chooseDoctor = appointmentService.setAppointmentDoctor(appointment,doctor,doctorDto.getSetDate(),doctorDto.getSetTime(),request);

        assertEquals("if it is equal","It is successfully",chooseDoctor);

     String chooseDoctor1 = appointmentService.setAppointmentDoctor(appointment,doctor,doctorDto1.getSetDate(),doctorDto1.getSetTime(),request);
       assertEquals("if it is equal","The doctor is busy at that time",chooseDoctor1);
    }
    @Test
    public void testUpdateAppointment(){
        Appointment appointment = Appointment.builder()
                .reason("heart-problem")
                .build();
        AppointmentUpdateRequest appointmentDto = AppointmentUpdateRequest.builder()
                .reason("lung-problem")
                .build();
//       doNothing().when(customerMapper).updateModelFromDto(customerDto,customer);
//       when(repo.save(customer)).thenReturn(customer);
        String updateAppointment = appointmentService.updateAppointment(appointment,appointmentDto);

        assertEquals("Update Appointment","It is updated successfully!", updateAppointment);


    }
}
