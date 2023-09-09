package com.example.healthcare.repository;

import com.example.healthcare.model.Doctor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoctorPagingRepository extends PagingAndSortingRepository<Doctor, UUID> {

}
