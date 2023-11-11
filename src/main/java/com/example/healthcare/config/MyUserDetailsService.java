package com.example.healthcare.config;

import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.repository.CustomerRepository;
import com.example.healthcare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private DoctorRepository doctorRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = null;
        Customer customer = customerRepo.findCustomerByUsername(username);
        Doctor doctor = doctorRepo.findByUsername(username);
        userDetails = User
                .withUsername("Nikola")
                .password(passwordEncoder().encode("nikola02"))
                .roles("ADMIN").build();

        if (customer != null) {
            userDetails = User
                    .withUsername(customer.getUsername())
                    .password(passwordEncoder().encode(customer.getPassword()))
                    .roles("CUSTOMER").build();
        } else if (doctor != null) {
            userDetails = User
                    .withUsername(doctor.getUsername())
                    .password(passwordEncoder().encode(doctor.getPassword()))
                    .roles("DOCTOR").build();
        }

        if (userDetails == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return userDetails;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}