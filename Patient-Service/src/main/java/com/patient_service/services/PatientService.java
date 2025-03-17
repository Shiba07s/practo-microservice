package com.patient_service.services;

import java.util.List;

import com.patient_service.dtos.PatientDto;

public interface PatientService {

	PatientDto createNewPatient(PatientDto patientDto);

	List<PatientDto> getALlDetails();

	PatientDto getPatient(int patientId);

	PatientDto updatePatient(int patientId, PatientDto patientDto);

}
