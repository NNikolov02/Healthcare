package com.example.healthcare.service;

import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.dto.SetRatingRequest;
import com.example.healthcare.dto.customer.CustomerCreateRequest;
import com.example.healthcare.dto.customer.CustomerUpdateRequest;
import com.example.healthcare.error.NotFoundObjectException;
import com.example.healthcare.mapping.CustomerMapper;
import com.example.healthcare.mapping.DoctorMapper;
import com.example.healthcare.model.*;
import com.example.healthcare.registration.customer.OnRegistrationCompleteEventCustomer;
import com.example.healthcare.repository.CustomerPagingRepository;
import com.example.healthcare.repository.CustomerRepository;
import com.example.healthcare.repository.DoctorRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Service
public class CustomerService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository repo;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerPagingRepository pagingRepo;
    @Autowired
    private DoctorMapper doctorMapper;
    @Autowired
    private DoctorRepository doctorRepo;

    public Page<Customer> fetchAll(int currentPage, int pageSize) {
        return pagingRepo.findAll(PageRequest.of(currentPage, pageSize));
    }

    public  Customer save(Customer customer){
        return repo.save(customer);
    }

    public Customer findById(String customerId) {
        return repo.findById(UUID.fromString(customerId)).orElse(null);

    }
    public Customer findByUserName(String name){
        return repo.findCustomerByUsername(name);
    }

    public void deleteById(String customerId){
        repo.deleteById(UUID.fromString(customerId));
    }

    @Transactional
    public void deleteByName(String name){
        repo.deleteCustomerByUsername(name);
    }

    public List<AvailableHoursDto> setAvailableHours(Doctor doctor){
        List<AvailableHours> hours = doctor.getAvailableHours();
        List<AvailableHoursDto> doctorHoursResponse = doctorMapper.responseFromModelHours(hours);

        return doctorHoursResponse;

    }
    public String registerUser(CustomerCreateRequest customerDto, HttpServletRequest request) {



        Customer create = customerMapper.modelFromCreateRequest(customerDto);
//        String rawPassword = create.getPassword();
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//        create.setPassword(encodedPassword);
        Customer saved = repo.save(create);

        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEventCustomer(saved, request.getLocale(), appUrl));

        return "Registration Successfully!";
    }
    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
    public void createVerificationToken(Customer customer, String token) {
        System.out.println("Creating verification token for customer: " + customer.getUsername());
        System.out.println("Token: " + token);
    }

    public String updateCustomer(Customer customer, CustomerUpdateRequest customerDto){
        customerMapper.updateModelFromDto(customerDto,customer);

        Customer saved = repo.save(customer);

        return "It is updated successfully!";


    }
    public String rating(Appointment appointment, Doctor doctor, SetRatingRequest request){

        if(appointment.getDoctor() == doctor) {


            List<Rating> ratings = doctor.getRatings();
            Rating newRating = Rating.builder()
                    .rating(request.getRating())
                    .doctor(doctor)
                    .build();
            ratings.add(newRating);
            Integer average = 0;

            for (Rating rating : ratings) {
                average += rating.getRating();

            }
            doctor.setRating(average / ratings.size());

            doctorRepo.save(doctor);
        }


        return "Rated successfully!";


    }
}