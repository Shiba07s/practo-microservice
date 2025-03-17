package com.appointment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "Patient-Service", path = "/api/v1/patient-profile/")
public interface PatientClient {
	
	@GetMapping("/{patientProfileId}")
	ResponseEntity<PatientDto> getPatientById(@PathVariable int patientProfileId);

}