package com.doctor_service.services;

import java.util.List;

import com.doctor_service.dtos.DoctorsDto;
import com.doctor_service.services.Impl.DoctorSearchCriteria;

public interface DoctorProfileService {

	DoctorsDto createDoctor(DoctorsDto doctorDto);

	DoctorsDto updateDoctor(Integer doctorId, DoctorsDto doctorDto);

	DoctorsDto getDoctorById(Integer doctorId);

	List<DoctorsDto> getAllDoctors();

	void deleteDoctorsProfile(Integer doctorId);

	List<DoctorsDto> getAllVerifiedDoctorsList();

	List<DoctorsDto> getAllUnVerifiedDoctorsList();

	DoctorsDto verifyDoctorsProfile(Integer doctorId);

	List<DoctorsDto> searchDoctors(DoctorSearchCriteria criteria);

}
