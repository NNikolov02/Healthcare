package com.example.healthcare.config;


import com.example.healthcare.model.Customer;
import com.example.healthcare.model.Doctor;
import com.example.healthcare.repository.CustomerRepository;
import com.example.healthcare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import static io.netty.util.CharsetUtil.encoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import static org.springframework.security.config.Customizer.withDefaults;


@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/healthcare/customers/registration").permitAll()
                                .requestMatchers("/healthcare/doctors/registration").permitAll()
                                .requestMatchers(HttpMethod.GET,"http://localhost:8083/healthcare/doctors/{doctorId}").hasAnyRole("ADMIN","DOCTOR")
                                .requestMatchers(HttpMethod.GET,"http://localhost:8083/healthcare/doctors/photo/{doctorUsername}").hasAnyRole("ADMIN","DOCTOR")
                                .requestMatchers(HttpMethod.GET,"http://localhost:8083/healthcare/doctors/catalogHours/{doctorUserName}").hasAnyRole("ADMIN","DOCTOR")
                                .requestMatchers(HttpMethod.DELETE,"http://localhost:8083/healthcare/doctors/email/{email}").hasAnyRole("ADMIN","DOCTOR")
                                .requestMatchers(HttpMethod.DELETE,"http://localhost:8083/healthcare/doctors/{doctorId}").hasAnyRole("ADMIN","DOCTOR")
                                .requestMatchers(HttpMethod.POST,"http://localhost:8083/healthcare/doctors/photo/{doctorUserName}").hasAnyRole("ADMIN","DOCTOR")
                                .requestMatchers(HttpMethod.POST,"http://localhost:8083/healthcare/doctors/appointments/{appointmentId}").hasAnyRole("ADMIN","DOCTOR")
                                .requestMatchers(HttpMethod.GET,"http://localhost:8083/healthcare/customers/{customerId}").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers(HttpMethod.GET,"http://localhost:8083/healthcare/customers/name/{customerName}").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers(HttpMethod.GET,"http://localhost:8083/healthcare/customers/catalogHours/{doctorLastName}").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers(HttpMethod.DELETE,"http://localhost:8083/healthcare/customers/{customerId}").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers(HttpMethod.DELETE,"http://localhost:8083/healthcare/customers/name/{customerName}").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers(HttpMethod.PATCH,"http://localhost:8083/healthcare/customers/{customerName}").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers(HttpMethod.PUT,"http://localhost:8083/healthcare/customers/{customerName}/rating/{doctorFirstName}/{doctorLastName}").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers(HttpMethod.GET,"http://localhost:8083/healthcare/appoitments/{appointmentId}").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers(HttpMethod.GET,"http://localhost:8083/healthcare/appoitments/customer/{customerName}").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers(HttpMethod.DELETE,"http://localhost:8083/healthcare/appoitments/{customerName}").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers(HttpMethod.POST,"http://localhost:8083/healthcare/appoitments/create/{name}").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers(HttpMethod.PATCH,"http://localhost:8083/healthcare/appoitments/{appointmentId}").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers(HttpMethod.PUT,"http://localhost:8083/healthcare/appoitments/{appointmentId}").hasAnyRole("ADMIN","CUSTOMER")

                                .anyRequest().authenticated()
                )
                .logout((logout) -> logout.logoutSuccessUrl("http://localhost:8083/healthcare/customers/logout"))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());


        return http.build();
    }


//    @Bean
//    public UserDetailsService customerUserDetailsService() {
//        return email -> {
//            // Load the user details by email from your database
//            Customer customer = customerRepo.findByEmail(email);
//
//            if (customer != null) {
//                return User.builder()
//                        .username(customer.getEmail()) // Set the username as the email
//                        .password(passwordEncoder().encode(customer.getPassword())) // Use the stored password
//                        .roles("CUSTOMER")
//                        .build();
//            } else {
//                throw new UsernameNotFoundException("Customer not found with email: " + email);
//            }
//        };
//    }
//
//    @Bean
//    public UserDetailsService doctorUserDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        return email -> {
//            // Load the user details by email from your database for doctors
//            Doctor doctor = doctorRepo.findAllByEmail(email);
//
//            if (doctor != null) {
//                return manager.createUser(User
//                        .withUsername(doctor.getEmail()) // Set the username as the email
//                        .password(passwordEncoder().encode(doctor.getPassword())) // Use the stored password
//                        .roles("DOCTOR")
//                        .build());
//            }
//            return null;
//            };

   // }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        return email -> {
//            Doctor doctor = doctorRepo.findAllByEmail(email);
//            Customer customer = customerRepo.findByEmail(email);
//
//            if (doctor != null) {
//                manager.createUser(User
//                        .withUsername(doctor.getUsername())
//                        .password(passwordEncoder().encode(doctor.getPassword()))
//                        .roles("DOCTOR")
//                        .build());
//            }
//
//            if (customer != null) {
//                manager.createUser(User
//                        .withUsername("Ivan")
//                        .password(passwordEncoder().encode("parola"))
//                        .roles("CUSTOMER")
//                        .build());
//            }
//
//            // Return the UserDetails if either the doctor or customer is found, or null if not found
//            return manager;
//        };
//    }

//    @Bean
//    public UserDetailsService userDetailsService() throws Exception {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//
//        manager.createUser(User
//                .withUsername("admin")
//                .password(passwordEncoder().encode("adminPass"))
//                .roles("ADMIN").build());
//        return manager;
//    }

//    @Bean
//    public UserDetailsService userDetailsService() throws Exception {
//        return username -> {
//            Customer customerOptional = customerRepo.findCustomerByUsername(username);
//            if (customerOptional == null) {
//                throw new UsernameNotFoundException("User not found with username: " + username);
//            }
//            return User
//                    .withUsername(customerOptional.getUsername())
//                    .password(passwordEncoder().encode(customerOptional.getPassword()))
//                    .roles("CUSTOMER").build();
//        };
//    }
//    @Bean
//    public UserDetailsService doctorDetailsService() throws Exception {
//        return username -> {
//            Doctor doctorOptional = doctorRepo.findByUsername(username);
//            if (doctorOptional == null) {
//                throw new UsernameNotFoundException("User not found with username: " + username);
//            }
//            return User
//                    .withUsername(doctorOptional.getUsername())
//                    .password(passwordEncoder().encode(doctorOptional.getPassword()))
//                    .roles("DOCTOR").build();
//        };
//    }
//
////        manager.createUser(User
////                .withUsername("admin")
////                .password(passwordEncoder().encode("adminPass"))
////                .roles("ADMIN").build());
////        return manager;
//
//

//    @Bean
//    public AuthenticationEntryPoint authenticationEntryPoint(){
//        BasicAuthenticationEntryPoint entryPoint =
//                new BasicAuthenticationEntryPoint();
//        entryPoint.setRealmName("doctor realm");
//        return entryPoint;
//    }


//        @Bean
//        public PasswordEncoder passwordEncoder() {
//            return new BCryptPasswordEncoder();
//        }
//        @Bean
//        public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
//            AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//            authenticationManagerBuilder.userDetailsService(customerUserDetailsService()).passwordEncoder(passwordEncoder());
//            authenticationManagerBuilder.userDetailsService(doctorUserDetailsService()).passwordEncoder(passwordEncoder());
//            return authenticationManagerBuilder.build();
//        }
}
