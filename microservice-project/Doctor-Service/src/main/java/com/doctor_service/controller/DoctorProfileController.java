package com.doctor_service.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doctor_service.dtos.DoctorStatus;
import com.doctor_service.dtos.DoctorsDto;
import com.doctor_service.services.DoctorProfileService;
import com.doctor_service.services.Impl.DoctorSearchCriteria;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/doctor-profile")
public class DoctorProfileController {

	private final DoctorProfileService doctorProfileService;

	// Create a new doctor profile
	@PostMapping("/create")
	public ResponseEntity<DoctorsDto> createDoctorsProfile(@RequestBody DoctorsDto doctorsDto) {
		DoctorsDto doctor = doctorProfileService.createDoctor(doctorsDto);
		return new ResponseEntity<>(doctor, HttpStatus.CREATED);
	}
	
	// Get a doctor profile by ID
	@GetMapping("/{doctorId}")
	public ResponseEntity<DoctorsDto> getDoctorById(@PathVariable Integer doctorId) {
		DoctorsDto getById = doctorProfileService.getDoctorById(doctorId);
		return new ResponseEntity<>(getById, HttpStatus.OK);
	}

	// Get the list of all doctors
	@GetMapping
	public ResponseEntity<List<DoctorsDto>> getDoctorsList() {
		List<DoctorsDto> allDoctors = doctorProfileService.getAllDoctors();
		return new ResponseEntity<>(allDoctors, HttpStatus.OK);
	}

	@PutMapping("/update/{doctorId}")
	public ResponseEntity<DoctorsDto> updateDoctor(@PathVariable Integer doctorId,
			@Validated @RequestBody DoctorsDto doctorDto) {
		DoctorsDto updatedDoctor = doctorProfileService.updateDoctor(doctorId, doctorDto);
		return new ResponseEntity<>(updatedDoctor, HttpStatus.OK);
	}

	// Delete a doctor profile by ID
	@DeleteMapping("/delete/{doctorId}")
	public ResponseEntity<Void> deleteDoctor(@PathVariable Integer doctorId) {
		doctorProfileService.deleteDoctorsProfile(doctorId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PutMapping("/{doctorId}/verify")
	public ResponseEntity<DoctorsDto> verifyDoctorsProfile(@PathVariable Integer doctorId) {
		DoctorsDto verifyDoctorsProfile = doctorProfileService.verifyDoctorsProfile(doctorId);
		return new ResponseEntity<>(verifyDoctorsProfile, HttpStatus.OK);
	}

	@GetMapping("/verified-list")
	public ResponseEntity<List<DoctorsDto>> getVerifiedDoctorsList() {
	  List<DoctorsDto> allVerifiedDoctorsList = doctorProfileService.getAllVerifiedDoctorsList();
		return new ResponseEntity<>(allVerifiedDoctorsList, HttpStatus.OK);
	}
	@GetMapping("/un-verified-list")
	public ResponseEntity<List<DoctorsDto>> getUnVerifiedDoctorsList() {
		List<DoctorsDto> allUnVerifiedDoctorsList = doctorProfileService.getAllUnVerifiedDoctorsList();
		return new ResponseEntity<>(allUnVerifiedDoctorsList, HttpStatus.OK);
	}

	@GetMapping("/search")
    public ResponseEntity<List<DoctorsDto>> searchDoctors(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String clinicName,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) BigDecimal maxFee,
            @RequestParam(required = false) DoctorStatus status) {

        DoctorSearchCriteria criteria = DoctorSearchCriteria.builder()
                .keyword(keyword)
                .specialization(specialization)
                .location(location)
                .clinicName(clinicName)
                .minExperience(minExperience)
                .maxFee(maxFee)
                .status(status)
                .build();

        List<DoctorsDto> doctors = doctorProfileService.searchDoctors(criteria);
        return ResponseEntity.ok(doctors);
    }
}