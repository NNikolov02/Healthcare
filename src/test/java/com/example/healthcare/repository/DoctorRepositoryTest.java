package com.example.healthcare.repository;

import com.example.healthcare.model.Appointment;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.model.Photo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void testFindAllByAvailable() {
        // Create a Doctor entity and save it to the database
        Doctor doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .available(true)
                .build();
        entityManager.persistAndFlush(doctor);


        List<Doctor> availableDoctors = doctorRepo.findAllByAvailable(true);


        assertThat(availableDoctors).contains(doctor);
    }

    @Test
    public void testFindAllByHospitalName(){
        Doctor doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .hospitalName("ST.Anna")
                .build();

        entityManager.persistAndFlush(doctor);
        List<Doctor> availableDoctors = doctorRepo.findAllByHospitalName("ST.Anna");

        assertThat(availableDoctors).contains(doctor);

    }
    @Test
    public void testFindAllBySpecialty(){
        Doctor doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .specialty("cadiolok")
                .build();

        entityManager.persistAndFlush(doctor);
        List<Doctor> availableDoctors = doctorRepo.findAllBySpecialty("cadiolok");

        assertThat(availableDoctors).contains(doctor);

    }


    @Test
    public void testFindAllByEmail(){
        Doctor doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .email("nikolaynikolov2002@gmail.com")
                .build();

        entityManager.persistAndFlush(doctor);
        Doctor availableDoctor = doctorRepo.findAllByEmail("nikolaynikolov2002@gmail.com");

        assertEquals("nikolaynikolov2002@gmail.com", availableDoctor.getEmail());

    }
    // Doctor deleteAllByEmail(String email);

    @Test
    public void testDeleteByEmail() {
        // Create and persist a Doctor with the given email
        Doctor doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .email("nikolaynikolov2002@gmail.com")
                .build();

        entityManager.persistAndFlush(doctor);

         doctorRepo.deleteByEmail("nikolaynikolov2002@gmail.com");

        Doctor nonExistentDoctor = doctorRepo.findAllByEmail("nikolaynikolov2002@gmail.com");

        assertNull(nonExistentDoctor);
    }
    @Test
    public void testFindByFirstNameAndLastName(){
        Doctor doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        entityManager.persistAndFlush(doctor);
        Doctor availableDoctor = doctorRepo.findByFirstNameAndLastName("John","Doe");

        assertNotNull(availableDoctor);


        assertEquals("John", availableDoctor.getFirstName());
        assertEquals("Doe", availableDoctor.getLastName());


    }
    @Test
    public void testFindByUsername(){
        Doctor doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .username("Ivan1234")
                .build();

        entityManager.persistAndFlush(doctor);
        Doctor availableDoctor = doctorRepo.findByUsername("Ivan1234");

        assertEquals("Ivan1234", availableDoctor.getUsername());

    }
    @Test
    public void testFindByLastName(){
        Doctor doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        entityManager.persistAndFlush(doctor);
        Doctor availableDoctor = doctorRepo.findByLastName("Doe");

        assertEquals("Doe", availableDoctor.getLastName());

    }

    @Test
    public void testFindByAppointmentId(){
        List <Appointment> appointments = new ArrayList<>();
        for(Appointment appointment:appointments){
            appointment =Appointment.builder().reason("dwdwwd").build();
            entityManager.persistAndFlush(appointment);
        }

        Doctor doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .appointments(appointments)
                .build();

        entityManager.persistAndFlush(doctor);
        for(Appointment appointment:appointments) {
            appointment.setDoctor(doctor);
            entityManager.persistAndFlush(appointment);
            Doctor availableDoctor = doctorRepo.findByAppointmentId(appointment.getId());
            assertEquals("Doe",availableDoctor.getLastName());
        }



    }
    @Test
    public void testFindDoctorsByCustomerId(){
        List <Appointment> appointments = new ArrayList<>();
        for(Appointment appointment:appointments){
            appointment =Appointment.builder().reason("dwdwwd").build();
            entityManager.persistAndFlush(appointment);
        }

        Doctor doctor = Doctor.builder()
                .firstName("John")
                .lastName("Doe")
                .appointments(appointments)
                .build();

        entityManager.persistAndFlush(doctor);
        for(Appointment appointment:appointments) {
            appointment.setDoctor(doctor);
            entityManager.persistAndFlush(appointment);
            Customer customer = appointment.getCustomer();
            List<Doctor> availableDoctors = doctorRepo.findDoctorsByCustomerIdList(customer.getId());
            assertThat(availableDoctors).contains(doctor);
            for(Doctor doctor1:availableDoctors){
                assertEquals("John",doctor1.getFirstName());
            }
        }



    }
    @Test
    public void testFindPhotoByDoctorUsername(){
        Doctor doctor = Doctor.builder()
                .username("Ivan")
                .firstName("John")
                .lastName("Doe")
                .build();
        entityManager.persistAndFlush(doctor);
        Photo photo =Photo.builder()
                .originalFilename("DoctorPhoto")
                .doctor(doctor)
                .build();
        doctor.setPhoto(photo);
        entityManager.persistAndFlush(doctor);


        entityManager.persistAndFlush(photo);

        Photo availablePhoto = doctorRepo.findPhotoByDoctorUsername("Ivan");

        assertEquals("DoctorPhoto", availablePhoto.getOriginalFilename());



    }

}
