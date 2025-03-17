package com.patient_service.services.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.patient_service.dtos.PatientDto;
import com.patient_service.entities.Patient;
import com.patient_service.repositories.PatientRepository;
import com.patient_service.services.PatientService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PatientServiceImpl implements PatientService {

	private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);

	private final PatientRepository patientRepository;
	private final ModelMapper modelMapper;

	@Override
	public PatientDto createNewPatient(PatientDto patientDto) {
		logger.info("Creating new patient: {}", patientDto);
		Patient map = modelMapper.map(patientDto, Patient.class);
		Patient save = patientRepository.save(map);
		logger.info("Patient created successfully: {}", save);
		return modelMapper.map(save, PatientDto.class);
	}

	@Override
	public List<PatientDto> getALlDetails() {
		logger.info("Fetching all patient details");
		List<Patient> all = patientRepository.findAll();
		List<PatientDto> patientDtos = all.stream()
				.map(patientDetails -> modelMapper.map(patientDetails, PatientDto.class)).collect(Collectors.toList());
		logger.info("Fetched {} patient(s)", patientDtos.size());
		return patientDtos;
	}

	@Override
	public PatientDto getPatient(int patientId) {
		logger.info("Fetching patient with ID: {}", patientId);
		Patient patient = patientRepository.findById(patientId)
				.orElseThrow(() -> new RuntimeException("This patient is not present with this id: " + patientId));
		logger.info("Fetched patient: {}", patient);
		return modelMapper.map(patient, PatientDto.class);
	}

	@Override
	public PatientDto updatePatient(int patientId, PatientDto patientDto) {
		logger.info("Updating patient with ID: {}", patientId);
		Patient patient = patientRepository.findById(patientId)
				.orElseThrow(() -> new RuntimeException("This patient is not present with this id: " + patientId));
		LocalDateTime createdAt = patient.getCreatedAt();
		modelMapper.map(patientDto, patient);
		patient.setCreatedAt(createdAt);
		Patient save = patientRepository.save(patient);
		logger.info("Patient updated successfully: {}", save);
		return modelMapper.map(save, PatientDto.class);
	}
}
//	private final PatientRepository patientRepository;
//	private final ModelMapper modelMapper;
//
//	@Override
//	public PatientDto createNewPatient(PatientDto patientDto) {
//
//		Patient map = modelMapper.map(patientDto, Patient.class);
//		System.out.println(map);
//		Patient save = patientRepository.save(map);
//		return modelMapper.map(save, PatientDto.class);
//	}
//
//	@Override
//	public List<PatientDto> getALlDetails() {
//		List<Patient> all = patientRepository.findAll();
//		return all.stream().map(patientDetails -> modelMapper.map(patientDetails, PatientDto.class))
//				.collect(Collectors.toList());
//	}
//
//	@Override
//	public PatientDto getPatient(int patientId) {
//		Patient patient = patientRepository.findById(patientId)
//				.orElseThrow(() -> new RuntimeException("This patient is not present with thus id : " + patientId));
//
//		return modelMapper.map(patient, PatientDto.class);
//	}
//
//	@Override
//	public PatientDto updatePatient(int patientId, PatientDto patientDto) {
//		Patient patient = patientRepository.findById(patientId)
//				.orElseThrow(() -> new RuntimeException("This patient is not present with thus id : " + patientId));
//		LocalDateTime createdAt = patient.getCreatedAt();
//		modelMapper.map(patientDto, patient);
//		patient.setCreatedAt(createdAt);
//		Patient save = patientRepository.save(patient);
//
//		return modelMapper.map(save, PatientDto.class);
//	}
//
//}
