package com.example.healthcare.web;

import com.example.healthcare.dto.AvailableHoursDto;
import com.example.healthcare.dto.doctor.*;
import com.example.healthcare.error.InvalidObjectException;
import com.example.healthcare.mapping.DoctorMapper;
import com.example.healthcare.model.*;

import com.example.healthcare.registration.customer.OnDoctorCompleteEventCustomerAccept;
import com.example.healthcare.registration.customer.OnDoctorCompleteEventCustomerDecline;
import com.example.healthcare.registration.doctor.OnRegistrationCompleteEventDoctor;
import com.example.healthcare.repository.AppointmentRepository;
import com.example.healthcare.repository.CustomerRepository;
import com.example.healthcare.repository.DoctorRepository;
import com.example.healthcare.service.DoctorService;
import com.example.healthcare.validation.ObjectValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/healthcare/doctors")
@AllArgsConstructor
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DoctorMapper doctorMapper;
    @Autowired
    private ObjectValidator validator;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private  CustomerRepository customerRepo;
    @Autowired
    private AppointmentRepository appointmentRepo;
    @Autowired
    private DoctorRepository doctorRepo;


    @GetMapping(value = "", produces = "application/json")
    public DoctorApiPage<DoctorResponse> getAllCarts(
            @RequestParam(required = false, defaultValue = "1") Integer currPage ){


        Page<DoctorResponse> doctorPage = doctorService.fetchAll(currPage - 1, 10).map(doctorMapper::responseFromModelOne);

        //for(DoctorResponse doctorResponse:doctorPage){

        //}


        return new DoctorApiPage<>(doctorPage);
    }

    @GetMapping(value ="/{doctorId}")
    public ResponseEntity<DoctorResponse>findById(@PathVariable String doctorId){

        Doctor doctor = doctorService.findById(doctorId);
        //UUID allPersonPhotoId = doctorService.getAllPersonPhotoIds(doctorId);
        DoctorResponse response = doctorMapper.responseFromModelOne(doctor);
        //response.setPersonPhotoId(allPersonPhotoId);



        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/photo/{doctorUsername}")
    @Transactional
    public ResponseEntity<byte[]> getPhotoById(@PathVariable String doctorUsername) {
        Photo photo = doctorRepo.findPhotoByDoctorUsername(doctorUsername);

        byte[] imageData = doctorService.retrieveImageData(doctorUsername);

        // Set appropriate content type (e.g., image/jpeg)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        // Return the binary data as a ResponseEntity
        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }

    @GetMapping(value ="/available/{available}")
    public ResponseEntity<List<DoctorResponse>>findTheAvailable(@PathVariable boolean available){

        List<Doctor>doctors = (List<Doctor>) doctorService.findByAvailable(available);

        return ResponseEntity.ok(doctorMapper.responseFromModelList(doctors));

    }

    @GetMapping(value ="/hospital/{hospitalName}")
    public ResponseEntity<List<DoctorResponse>>findByHospitalName(@PathVariable String hospitalName){
        List<Doctor>doctors = (List<Doctor>) doctorService.findByHospitalName(hospitalName);

       return ResponseEntity.ok(doctorMapper.responseFromModelList(doctors));
    }

    @GetMapping("/catalogHours/{doctorUserName}")
    public ResponseEntity<List<AvailableHoursDto>>findCatalog(@PathVariable String doctorUserName){
        Doctor doctor = doctorService.findByName(doctorUserName);
        List<AvailableHoursDto>doctorHours =doctorService.setAvailableHours(doctor);

        return ResponseEntity.ok().body(doctorHours);
    }



    @DeleteMapping("/{doctorId}")
    public ResponseEntity<String>deleteById(@PathVariable String doctorId){

        doctorService.deleteById(doctorId);

        return ResponseEntity.ok("It is deleted!");
    }
    @DeleteMapping("/email/{email}")
    public ResponseEntity<String>deleteByEmail(@PathVariable String email){

        doctorService.deleteByEmail(email);

        return ResponseEntity.ok("It is deleted!");
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
    @PostMapping("photo/{doctorUserName}")
    public ResponseEntity<String> handleImageUpload(@RequestParam("image") MultipartFile file,
                                                    RedirectAttributes redirectAttributes,@PathVariable String doctorUserName) throws IOException {
        Doctor doctor = doctorService.findByName(doctorUserName);

        Photo photoDto = Photo.builder()
                .originalFilename(file.getOriginalFilename())
                .content(file.getBytes())
                .build();

        doctor.setPhoto(photoDto);
        doctorService.save(doctor);

        return ResponseEntity.ok("It is successfully!");
    }
    @PostMapping ("/appointments/{appointmentId}")
    public ResponseEntity<String> acceptApp(@PathVariable String appointmentId,@RequestParam boolean setAccept, HttpServletRequest request) {
        List<Customer> customers = (List<Customer>) customerRepo.findByAppointmentId(UUID.fromString(appointmentId)); // Change to customerService
        Doctor doctor = doctorRepo.findByAppointmentId(UUID.fromString(appointmentId));
        String firstName = doctor.getFirstName();
        String lastName = doctor.getLastName();
        List<Appointment> appointments = appointmentRepo.findAllById(UUID.fromString(appointmentId));
        String setAcc = doctorService.setApp(doctor,customers,appointments,setAccept,request);

        return ResponseEntity.ok().body(setAcc);
    }

}
