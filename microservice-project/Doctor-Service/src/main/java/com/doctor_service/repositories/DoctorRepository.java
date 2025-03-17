package com.doctor_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.doctor_service.entities.Doctors;

//public interface DoctorRepository extends JpaRepository<Doctors, Integer> {
public interface DoctorRepository extends JpaRepository<Doctors, Integer>, JpaSpecificationExecutor<Doctors> {

}
