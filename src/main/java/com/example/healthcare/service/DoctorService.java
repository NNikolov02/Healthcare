package com.example.healthcare.service;

import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.error.NotFoundObjectException;
import com.example.healthcare.mapping.DoctorMapper;
import com.example.healthcare.model.*;
import com.example.healthcare.registration.customer.OnDoctorCompleteEventCustomerAccept;
import com.example.healthcare.registration.customer.OnDoctorCompleteEventCustomerDecline;
import com.example.healthcare.registration.doctor.OnRegistrationCompleteEventDoctor;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.DoctorPagingRepository;
import com.example.healthcare.repository.DoctorRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.sql.Timestamp;
import java.util.*;

@Component
@Service
public class DoctorService {

    @Autowired
    private DoctorRepository repo;

    @Autowired
    private DoctorPagingRepository pagingRepo;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private AppointmentRepository appointmentRepo;
    @Autowired
    private DoctorMapper doctorMapper;

    public Page<Doctor> fetchAll(int currentPage, int pageSize) {
        return pagingRepo.findAll(PageRequest.of(currentPage, pageSize));
    }

    public Doctor save(Doctor doctor){
        return repo.save(doctor);
    }

    public Doctor findById(String doctorId) {
        return repo.findById(UUID.fromString(doctorId)).orElseThrow(() -> {
            throw new NotFoundObjectException("Doctor Not Found", Doctor.class.getName(), doctorId);
        });
    }
    public Doctor findByEmail(String email){
        return repo.findAllByEmail(email);
    }
    public Doctor findByName(String name){
        return repo.findByUsername(name);
    }
    public byte[] retrieveImageData(String username) {
        // Implement the logic to retrieve binary image data from the database
        // For example, you can retrieve it by ID from the repository
        // Replace "yourId" with the actual ID you want to retrieve
        Long yourId = 1L; // Replace with your desired ID
        Photo imageEntity = repo.findPhotoByDoctorUsername(username);

        if (imageEntity != null) {
            return imageEntity.getContent();
        } else {
            // Handle the case where the image data is not found (return null or throw an exception)
            return null;
        }
    }
    public UUID getAllPersonPhotoIds(String personId) {
        Doctor doctor = repo.findById(UUID.fromString(personId)).get();
        Photo photo =doctor.getPhoto();

        UUID allPersonPhotoId = photo.getId();


        return allPersonPhotoId;
    }

    public List<Doctor>findByHospitalName(String name){
        return repo.findAllByHospitalName(name);
    }

    public List<Doctor>findByAvailable(boolean available){

        return repo.findAllByAvailable(available);
    }
    public List<Doctor>findBySpecialty(String specialty){
        return repo.findAllBySpecialty(specialty);
    }
    public void deleteById(String doctorId){
        repo.deleteById(UUID.fromString(doctorId));
    }
    public void deleteByEmail(String email){
        repo.deleteByEmail(email);
    }
    public String connectHours(Doctor create,List<AvailableHours>doctorHours,HttpServletRequest request){
        doctorHours = create.getAvailableHours();


        for(AvailableHours availableHours:doctorHours) {
            availableHours.setDoctor(create);
            availableHours.setFirstName(create.getFirstName());
            availableHours.setLastName(create.getLastName());
            availableHours.setHours(availableHours.getHours());



        }

        Doctor saved = repo.save(create);



        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEventDoctor(saved,
                request.getLocale(), appUrl));


        return "Registration Successfully!";



    }
    public List<AvailableHoursDto> setAvailableHours(Doctor doctor){
        List<AvailableHours> hours = doctor.getAvailableHours();
        List<AvailableHoursDto> doctorHoursResponse = doctorMapper.responseFromModelHours(hours);
        for(AvailableHoursDto availableHoursDto:doctorHoursResponse) {
            for (AvailableHours availableHours : hours) {
                availableHoursDto.setDate(availableHours.getDate());
                availableHoursDto.setHours(availableHours.getHours());
            }
        }
        //doctorHoursResponse.setFirstName(doctor.getFirstName());
        // doctorHoursResponse.setLastName(doctor.getLastName());

        return doctorHoursResponse;

    }


    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
    public void createVerificationToken(Doctor doctor, String token) {
        System.out.println("Creating verification token for doctor: " + doctor.getFirstName() +" "+ doctor.getLastName());
        System.out.println("Token: " + token);
    }
    public  String setApp(Doctor doctor, List<Customer>customers, List<Appointment>appointments, boolean setAccept, HttpServletRequest request){

        for(Customer customer:customers) {

            if (customer != null && doctor != null) {
                if (setAccept) {
                    List<AvailableHours> availableHours = doctor.getAvailableHours();
                    for (AvailableHours availableHours1 : availableHours) {
                        for(Appointment appointment:appointments) {
                            if (availableHours1.getDate().equals(appointment.getStartDate()) && availableHours1.getHours().contains(appointment.getStartTime())) {
                                availableHours1.getHours().remove(appointment.getStartTime());
                                repo.save(doctor);
                                //appointment.setDoctor(doctor);
                                //appointmentRepo.save(appointment);

                            }
                        }
                    }
                    doctor.setAvailable(false);
                    repo.save(doctor);
                    String appUrl = request.getContextPath();
                    eventPublisher.publishEvent(new OnDoctorCompleteEventCustomerAccept(customer, request.getLocale(), appUrl));
                    return "The appointment is accepted";

                } else {
                    for (Appointment appointment : appointments) {
                        //Appointment appointment1 = doctor.getAppointments();

                        String appUrl = request.getContextPath();
                        eventPublisher.publishEvent(new OnDoctorCompleteEventCustomerDecline(customer, request.getLocale(), appUrl));
                        appointment.setStartTime(null);
                        appointment.setStartDate(null);
                        if(doctor.getAppointments().contains(appointment)){
                            doctor.getAppointments().remove(appointment);
                        }
                        repo.save(doctor);
                        appointment.setDoctor(null);

                        appointmentRepo.save(appointment);

                        return "The appointment is not accepted";
                    }
                }
            }

        }
        return null;
    }
}
