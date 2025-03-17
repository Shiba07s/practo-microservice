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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DoctorProfileServiceImpl implements DoctorProfileService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorProfileServiceImpl.class);  // Initialize logger

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;

    @Override
    public DoctorsDto createDoctor(DoctorsDto doctorDto) {
        try {
            logger.info("Creating doctor profile: {}", doctorDto.getFirstName()+" "+doctorDto.getLastName()); // Log the doctor creation request

            Doctors map = modelMapper.map(doctorDto, Doctors.class);
            map.setStatus(DoctorStatus.UNVERIFIED);
            Doctors save = doctorRepository.save(map);
            
            // initiateVerification(save); // Commented as per original code

            logger.info("Doctor profile created successfully with ID: {}", save.getId());
            return modelMapper.map(save, DoctorsDto.class);
        } catch (Exception e) {
            logger.error("Error creating doctor profile: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating doctor profile", e);  // Optionally rethrow or handle error
        }
    }

    private void verfiDoctorsStatus(Integer id) {
        try {
            logger.info("Verifying doctor profile with ID: {}", id);
            Doctors doctorsDetails = doctorRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor is not present with this ID: " + id));
            
            doctorsDetails.setStatus(DoctorStatus.VERIFIED);
            doctorRepository.save(doctorsDetails);
            logger.info("Doctor profile with ID: {} verified successfully.", id);
        } catch (Exception e) {
            logger.error("Error verifying doctor profile with ID: {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error verifying doctor profile", e);
        }
    }

    @Override
    public DoctorsDto updateDoctor(Integer doctorId, DoctorsDto doctorDto) {
        try {
            logger.info("Updating doctor profile with ID: {}", doctorId);

            Doctors doctorsDetails = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor is not present with this ID: " + doctorId));

            DoctorStatus status = doctorsDetails.getStatus();
            var createdAt = doctorsDetails.getCreatedAt();
            modelMapper.map(doctorDto, doctorsDetails);
            doctorsDetails.setStatus(status);
            doctorsDetails.setCreatedAt(createdAt);

            Doctors update = doctorRepository.save(doctorsDetails);
            logger.info("Doctor profile with ID: {} updated successfully.", doctorId);

            return modelMapper.map(update, DoctorsDto.class);
        } catch (Exception e) {
            logger.error("Error updating doctor profile with ID: {}: {}", doctorId, e.getMessage(), e);
            throw new RuntimeException("Error updating doctor profile", e);
        }
    }

    @Override
    public DoctorsDto getDoctorById(Integer doctorId) {
        try {
            logger.info("Fetching doctor profile with ID: {}", doctorId);

            Doctors doctorsDetails = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor is not present with this ID: " + doctorId));

            logger.info("Doctor profile fetched successfully for ID: {}", doctorId);
            return modelMapper.map(doctorsDetails, DoctorsDto.class);
        } catch (Exception e) {
            logger.error("Error fetching doctor profile with ID: {}: {}", doctorId, e.getMessage(), e);
            throw new RuntimeException("Error fetching doctor profile", e);
        }
    }

    @Override
    public List<DoctorsDto> getAllDoctors() {
        try {
            logger.info("Fetching list of all doctors.");
            List<Doctors> all = doctorRepository.findAll();
            logger.info("Fetched {} doctors.", all.size());
            return all.stream().map(doctors -> modelMapper.map(doctors, DoctorsDto.class)).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error fetching all doctors: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching all doctors", e);
        }
    }

    @Override
    public void deleteDoctorsProfile(Integer doctorId) {
        try {
            logger.info("Deleting doctor profile with ID: {}", doctorId);

            Doctors doctorsDetails = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor is not present with this ID: " + doctorId));

            doctorRepository.delete(doctorsDetails);
            logger.info("Doctor profile with ID: {} deleted successfully.", doctorId);
        } catch (Exception e) {
            logger.error("Error deleting doctor profile with ID: {}: {}", doctorId, e.getMessage(), e);
            throw new RuntimeException("Error deleting doctor profile", e);
        }
    }

    @Override
    public List<DoctorsDto> getAllVerifiedDoctorsList() {
        try {
            logger.info("Fetching list of verified doctors.");
            List<Doctors> doctorsList = doctorRepository.findAll();
            List<DoctorsDto> verifiedDoctors = doctorsList.stream()
                    .filter(status -> status.getStatus().equals(DoctorStatus.VERIFIED))
                    .map(doctors -> modelMapper.map(doctors, DoctorsDto.class))
                    .collect(Collectors.toList());

            logger.info("Fetched {} verified doctors.", verifiedDoctors.size());
            return verifiedDoctors;
        } catch (Exception e) {
            logger.error("Error fetching verified doctors list: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching verified doctors list", e);
        }
    }

    @Override
    public List<DoctorsDto> getAllUnVerifiedDoctorsList() {
        try {
            logger.info("Fetching list of unverified doctors.");
            List<Doctors> doctorsList = doctorRepository.findAll();
            List<DoctorsDto> unVerifiedDoctors = doctorsList.stream()
                    .filter(status -> status.getStatus().equals(DoctorStatus.UNVERIFIED))
                    .map(doctors -> modelMapper.map(doctors, DoctorsDto.class))
                    .collect(Collectors.toList());

            logger.info("Fetched {} unverified doctors.", unVerifiedDoctors.size());
            return unVerifiedDoctors;
        } catch (Exception e) {
            logger.error("Error fetching unverified doctors list: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching unverified doctors list", e);
        }
    }

    @Override
    public DoctorsDto verifyDoctorsProfile(Integer doctorId) {
        try {
            logger.info("Verifying doctor profile with ID: {}", doctorId);

            Doctors doctorsDetails = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor is not present with this ID: " + doctorId));

            doctorsDetails.setStatus(DoctorStatus.VERIFIED);
            Doctors updateStatus = doctorRepository.save(doctorsDetails);

            logger.info("Doctor profile with ID: {} verified successfully.", doctorId);
            return modelMapper.map(updateStatus, DoctorsDto.class);
        } catch (Exception e) {
            logger.error("Error verifying doctor profile with ID: {}: {}", doctorId, e.getMessage(), e);
            throw new RuntimeException("Error verifying doctor profile", e);
        }
    }

    @Override
    public List<DoctorsDto> searchDoctors(DoctorSearchCriteria criteria) {
        try {
            logger.info("Searching doctors with criteria: {}", criteria);
            Specification<Doctors> spec = DoctorSpecification.buildSpecification(criteria);
            List<Doctors> doctors = doctorRepository.findAll(spec);
            logger.info("Found {} doctors matching the search criteria.", doctors.size());
            return doctors.stream().map(doctor -> modelMapper.map(doctor, DoctorsDto.class)).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching doctors with criteria: {}: {}", criteria, e.getMessage(), e);
            throw new RuntimeException("Error searching doctors", e);
        }
    }
}

//@RequiredArgsConstructor
//@Service
//public class DoctorProfileServiceImpl implements DoctorProfileService {
//
//	private final DoctorRepository doctorRepository;
//	private final ModelMapper modelMapper;
//
//	@Override
//	public DoctorsDto createDoctor(DoctorsDto doctorDto) {
//
//		Doctors map = modelMapper.map(doctorDto, Doctors.class);
//		map.setStatus(DoctorStatus.UNVERIFIED);
//		Doctors save = doctorRepository.save(map);
////		initiateVerification(save);
//		return modelMapper.map(save, DoctorsDto.class);
//	}
//
////	private void initiateVerification(Doctors save) {
////
////		try {
////			Thread.sleep(20000);
////
////			verfiDoctorsStatus(save.getId());
////		} catch (Exception e) {
////		}
////	}
//
//	private void verfiDoctorsStatus(Integer id) {
//
//		Doctors doctorsDetails = doctorRepository.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException("Doctors is not present with this id : " + id));
//
//		doctorsDetails.setStatus(DoctorStatus.VERIFIED);
//		doctorRepository.save(doctorsDetails);
//	}
//
//	@Override
//	public DoctorsDto updateDoctor(Integer doctorId, DoctorsDto doctorDto) {
//		Doctors doctorsDetails = doctorRepository.findById(doctorId)
//				.orElseThrow(() -> new ResourceNotFoundException("Doctors is not present with this id : " + doctorId));
//
//		DoctorStatus status = doctorsDetails.getStatus();
//		var createdAt = doctorsDetails.getCreatedAt();
//		modelMapper.map(doctorDto, doctorsDetails);
//		doctorsDetails.setStatus(status);
//		doctorsDetails.setCreatedAt(createdAt);
//		Doctors update = doctorRepository.save(doctorsDetails);
//		return modelMapper.map(update, DoctorsDto.class);
//	}
//
//	@Override
//	public DoctorsDto getDoctorById(Integer doctorId) {
//		Doctors doctorsDetails = doctorRepository.findById(doctorId)
//				.orElseThrow(() -> new ResourceNotFoundException("Doctors is not present with this id : " + doctorId));
//		return modelMapper.map(doctorsDetails, DoctorsDto.class);
//	}
//
//	@Override
//	public List<DoctorsDto> getAllDoctors() {
//		List<Doctors> all = doctorRepository.findAll();
//		return all.stream().map(doctors -> modelMapper.map(doctors, DoctorsDto.class)).collect(Collectors.toList());
//	}
//
//	@Override
//	public void deleteDoctorsProfile(Integer doctorId) {
//		Doctors doctorsDetails = doctorRepository.findById(doctorId)
//				.orElseThrow(() -> new ResourceNotFoundException("Doctors is not present with this id : " + doctorId));
//
//		doctorRepository.delete(doctorsDetails);
//	}
//
//	@Override
//	public List<DoctorsDto> getAllVerifiedDoctorsList() {
//		List<Doctors> doctorsList = doctorRepository.findAll();
//		return doctorsList.stream().filter(status ->status.getStatus().equals(DoctorStatus.VERIFIED))
//				.map(doctors -> modelMapper.map(doctors, DoctorsDto.class)).collect(Collectors.toList());
//	}
//
//	@Override
//	public List<DoctorsDto> getAllUnVerifiedDoctorsList() {
//		List<Doctors> doctorsList = doctorRepository.findAll();
//		return doctorsList.stream().filter(status ->status.getStatus().equals(DoctorStatus.UNVERIFIED))
//				.map(doctors -> modelMapper.map(doctors, DoctorsDto.class)).collect(Collectors.toList());
//	}
//
//	@Override
//	public DoctorsDto verifyDoctorsProfile(Integer doctorId) {
//		Doctors doctorsDetails = doctorRepository.findById(doctorId)
//				.orElseThrow(() -> new ResourceNotFoundException("Doctors is not present with this id : " + doctorId));
//
//		doctorsDetails.setStatus(DoctorStatus.VERIFIED);
//		Doctors updateStatus = doctorRepository.save(doctorsDetails);
//		return modelMapper.map(updateStatus, DoctorsDto.class);
//	}
//	
//	@Override
//	public List<DoctorsDto> searchDoctors(DoctorSearchCriteria criteria) {
//		Specification<Doctors> spec = DoctorSpecification.buildSpecification(criteria);
//		return doctorRepository.findAll(spec).stream().map(doctor -> modelMapper.map(doctor, DoctorsDto.class))
//				.collect(Collectors.toList());
//	}
//
//}
