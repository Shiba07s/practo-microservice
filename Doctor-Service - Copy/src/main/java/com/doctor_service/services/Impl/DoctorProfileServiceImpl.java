package com.doctor_service.services.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.doctor_service.dtos.DoctorStatus;
import com.doctor_service.dtos.DoctorsDto;
import com.doctor_service.entities.Doctors;
import com.doctor_service.exception.ResourceNotFoundException;
import com.doctor_service.repositories.DoctorRepository;
import com.doctor_service.services.DoctorProfileService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DoctorProfileServiceImpl implements DoctorProfileService {

	private final DoctorRepository doctorRepository;
	private final ModelMapper modelMapper;

	@Override
	public DoctorsDto createDoctor(DoctorsDto doctorDto) {

		Doctors map = modelMapper.map(doctorDto, Doctors.class);
		map.setStatus(DoctorStatus.UNVERIFIED);
		Doctors save = doctorRepository.save(map);
//		initiateVerification(save);
		return modelMapper.map(save, DoctorsDto.class);
	}

//	private void initiateVerification(Doctors save) {
//
//		try {
//			Thread.sleep(20000);
//
//			verfiDoctorsStatus(save.getId());
//		} catch (Exception e) {
//		}
//	}

	private void verfiDoctorsStatus(Integer id) {

		Doctors doctorsDetails = doctorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Doctors is not present with this id : " + id));

		doctorsDetails.setStatus(DoctorStatus.VERIFIED);
		doctorRepository.save(doctorsDetails);
	}

	@Override
	public DoctorsDto updateDoctor(Integer doctorId, DoctorsDto doctorDto) {
		Doctors doctorsDetails = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Doctors is not present with this id : " + doctorId));

		DoctorStatus status = doctorsDetails.getStatus();
		var createdAt = doctorsDetails.getCreatedAt();
		modelMapper.map(doctorDto, doctorsDetails);
		doctorsDetails.setStatus(status);
		doctorsDetails.setCreatedAt(createdAt);
		Doctors update = doctorRepository.save(doctorsDetails);
		return modelMapper.map(update, DoctorsDto.class);
	}

	@Override
	public DoctorsDto getDoctorById(Integer doctorId) {
		Doctors doctorsDetails = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Doctors is not present with this id : " + doctorId));
		return modelMapper.map(doctorsDetails, DoctorsDto.class);
	}

	@Override
	public List<DoctorsDto> getAllDoctors() {
		List<Doctors> all = doctorRepository.findAll();
		return all.stream().map(doctors -> modelMapper.map(doctors, DoctorsDto.class)).collect(Collectors.toList());
	}

	@Override
	public void deleteDoctorsProfile(Integer doctorId) {
		Doctors doctorsDetails = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Doctors is not present with this id : " + doctorId));

		doctorRepository.delete(doctorsDetails);
	}

	@Override
	public List<DoctorsDto> getAllVerifiedDoctorsList() {
		List<Doctors> doctorsList = doctorRepository.findAll();
		return doctorsList.stream().filter(status ->status.getStatus().equals(DoctorStatus.VERIFIED))
				.map(doctors -> modelMapper.map(doctors, DoctorsDto.class)).collect(Collectors.toList());
	}

	@Override
	public List<DoctorsDto> getAllUnVerifiedDoctorsList() {
		List<Doctors> doctorsList = doctorRepository.findAll();
		return doctorsList.stream().filter(status ->status.getStatus().equals(DoctorStatus.UNVERIFIED))
				.map(doctors -> modelMapper.map(doctors, DoctorsDto.class)).collect(Collectors.toList());
	}

	@Override
	public DoctorsDto verifyDoctorsProfile(Integer doctorId) {
		Doctors doctorsDetails = doctorRepository.findById(doctorId)
				.orElseThrow(() -> new ResourceNotFoundException("Doctors is not present with this id : " + doctorId));

		doctorsDetails.setStatus(DoctorStatus.VERIFIED);
		Doctors updateStatus = doctorRepository.save(doctorsDetails);
		return modelMapper.map(updateStatus, DoctorsDto.class);
	}
	
	@Override
	public List<DoctorsDto> searchDoctors(DoctorSearchCriteria criteria) {
		Specification<Doctors> spec = DoctorSpecification.buildSpecification(criteria);
		return doctorRepository.findAll(spec).stream().map(doctor -> modelMapper.map(doctor, DoctorsDto.class))
				.collect(Collectors.toList());
	}

}
