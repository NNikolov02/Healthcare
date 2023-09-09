package com.example.healthcare.service;

import com.example.healthcare.error.NotFoundObjectException;
import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.repository.DoctorPagingRepository;
import com.example.healthcare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Service
public class DoctorService {

    @Autowired
    private DoctorRepository repo;

    @Autowired
    private DoctorPagingRepository pagingRepo;


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
        repo.deleteAllByEmail(email);
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


}
