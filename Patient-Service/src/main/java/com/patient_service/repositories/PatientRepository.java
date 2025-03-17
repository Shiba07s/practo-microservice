package com.patient_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patient_service.entities.Patient;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

}
