package com.appointment_service.services.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.appointment_service.client.DoctorClient;
import com.appointment_service.client.DoctorStatus;
import com.appointment_service.client.DoctorsDto;
import com.appointment_service.client.PatientClient;
import com.appointment_service.client.PatientDto;
import com.appointment_service.dtos.AppointmentDto;
import com.appointment_service.dtos.AppointmentStatus;
import com.appointment_service.entities.Appointment;
import com.appointment_service.exception.ResourceNotFoundException;
import com.appointment_service.repositories.AppointmentRepository;
import com.appointment_service.services.AppointmentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AppointmentServiceImpl implements AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final DoctorClient doctorClient;
	private final PatientClient patientClient;
	private final ModelMapper modelMapper;

	@Override
	public AppointmentDto bookAppointment(AppointmentDto appointmentDto) {
		// Fetch Doctor using Feign client
		ResponseEntity<DoctorsDto> doctorResponse = doctorClient.getDoctorsById(appointmentDto.getDoctorId());
		if (doctorResponse == null || doctorResponse.getBody() == null) {
			throw new ResourceNotFoundException("Doctor not found");
		}
		DoctorsDto doctorDto = doctorResponse.getBody();

		// Fetch Patient using Feign client
		ResponseEntity<PatientDto> patientResponse = patientClient.getPatientById(appointmentDto.getPatientId());
		if (patientResponse == null || patientResponse.getBody() == null) {
			throw new ResourceNotFoundException("Patient not found");
		}
		PatientDto patientDto = patientResponse.getBody();

		// Check if Doctor is verified
		if (!doctorDto.getStatus().equals(DoctorStatus.VERIFIED)) {
			throw new ResourceNotFoundException("Doctor is not verified. Cannot book an appointment.");
		}

		// Create new Appointment entity
		Appointment appointment = new Appointment();
		appointment.setDoctorId(doctorDto.getId());
		appointment.setPatientId(patientDto.getId());
		appointment.setStatus(AppointmentStatus.BOOKED);
		appointment.setAppointmentDate(appointmentDto.getAppointmentDate());
		appointment.setStartTime(appointmentDto.getStartTime());
		appointment.setEndTime(appointmentDto.getEndTime());
		appointment.setConsultationType(appointmentDto.getConsultationType());
		appointment.setConsultationLink(appointmentDto.getConsultationLink());
		appointment.setConsultationFee(doctorDto.getConsultationFee());

		// Save the Appointment
		Appointment savedAppointment = appointmentRepository.save(appointment);

		// Map entity to DTO
		AppointmentDto responseDto = modelMapper.map(savedAppointment, AppointmentDto.class);

		// Set doctor and patient DTOs in the response
		responseDto.setDoctor(doctorDto);
		responseDto.setPatient(patientDto);

		return responseDto;
	}

	@Override
	public AppointmentDto getAppointment(Integer appointmentId) {
		Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(
				() -> new ResourceNotFoundException("Appointment is not found for this id : " + appointmentId));

		ResponseEntity<DoctorsDto> doctorResponse = doctorClient.getDoctorsById(appointment.getDoctorId());
		if (doctorResponse == null || doctorResponse.getBody() == null) {
			throw new ResourceNotFoundException("Doctor not found");
		}
		DoctorsDto doctorDto = doctorResponse.getBody();

		// Fetch Patient using Feign client
		ResponseEntity<PatientDto> patientResponse = patientClient.getPatientById(appointment.getPatientId());
		if (patientResponse == null || patientResponse.getBody() == null) {
			throw new ResourceNotFoundException("Patient not found");
		}
		PatientDto patientDto = patientResponse.getBody();

		AppointmentDto responseDto = modelMapper.map(appointment, AppointmentDto.class);
		responseDto.setDoctor(doctorDto);
		responseDto.setPatient(patientDto);
		return responseDto;
	}

	@Override
	public List<AppointmentDto> getAllAppointments() {
		List<Appointment> all = appointmentRepository.findAll();
		return all.stream().map(appointment -> {
			ResponseEntity<DoctorsDto> doctorResponse = doctorClient.getDoctorsById(appointment.getDoctorId());
			if (doctorResponse == null || doctorResponse.getBody() == null) {
				throw new ResourceNotFoundException("Doctor not found");
			}
			DoctorsDto doctorDto = doctorResponse.getBody();

			// Fetch Patient using Feign client
			ResponseEntity<PatientDto> patientResponse = patientClient.getPatientById(appointment.getPatientId());
			if (patientResponse == null || patientResponse.getBody() == null) {
				throw new ResourceNotFoundException("Patient not found");
			}
			PatientDto patientDto = patientResponse.getBody();

			AppointmentDto responseDto = modelMapper.map(appointment, AppointmentDto.class);
			responseDto.setDoctor(doctorDto);
			responseDto.setPatient(patientDto);

			return responseDto;
		}).collect(Collectors.toList());
	}

	@Override
	public AppointmentDto updateAppointment(Integer appointmentId, AppointmentDto appointmentDto) {

		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

		LocalDateTime createdAt = appointment.getCreatedAt();
		if (appointmentDto.getDoctorId() != null && !appointmentDto.getDoctorId().equals(appointment.getDoctorId())) {
			 
			ResponseEntity<DoctorsDto> doctorResponse = doctorClient.getDoctorsById(appointmentDto.getDoctorId());
			if (doctorResponse == null || doctorResponse.getBody() == null) {
				throw new ResourceNotFoundException("Doctor not found with id: " + appointmentDto.getDoctorId());
			}

			DoctorsDto doctorDto = doctorResponse.getBody();
			if (!doctorDto.getStatus().equals(DoctorStatus.VERIFIED)) {
				throw new IllegalArgumentException("Doctor is not verified. Cannot assign appointment.");
			}

 			appointment.setDoctorId(doctorDto.getId());
			appointment.setConsultationFee(doctorDto.getConsultationFee());
		}

 		if (appointmentDto.getPatientId() != null
				&& !appointmentDto.getPatientId().equals(appointment.getPatientId())) {
 			
 			ResponseEntity<PatientDto> patientResponse = patientClient.getPatientById(appointmentDto.getPatientId());
			if (patientResponse == null || patientResponse.getBody() == null) {
				throw new ResourceNotFoundException("Patient not found with id: " + appointmentDto.getPatientId());
			}

			appointment.setPatientId(patientResponse.getBody().getId());
		}

		// Update appointment fields if provided in the DTO
			appointment.setAppointmentDate(appointmentDto.getAppointmentDate());
			appointment.setStartTime(appointmentDto.getStartTime());
			appointment.setEndTime(appointmentDto.getEndTime());
			appointment.setConsultationType(appointmentDto.getConsultationType());
			appointment.setConsultationLink(appointmentDto.getConsultationLink());
			appointment.setStatus(appointmentDto.getStatus());
			appointment.setCancellationReason(appointmentDto.getCancellationReason());
			appointment.setCreatedAt(createdAt);

		// If status is being changed to CANCELLED, ensure cancellation reason is
		// provided
		if (AppointmentStatus.CANCELLED.equals(appointmentDto.getStatus())
				&& (appointmentDto.getCancellationReason() == null
						|| appointmentDto.getCancellationReason().trim().isEmpty())) {
			throw new IllegalArgumentException("Cancellation reason must be provided when cancelling an appointment");
		}

		Appointment updatedAppointment = appointmentRepository.save(appointment);

		ResponseEntity<DoctorsDto> doctorResponse = doctorClient.getDoctorsById(updatedAppointment.getDoctorId());
		if (doctorResponse == null || doctorResponse.getBody() == null) {
			throw new ResourceNotFoundException("Doctor not found");
		}
		DoctorsDto doctorDto = doctorResponse.getBody();

		ResponseEntity<PatientDto> patientResponse = patientClient.getPatientById(updatedAppointment.getPatientId());
		if (patientResponse == null || patientResponse.getBody() == null) {
			throw new ResourceNotFoundException("Patient not found");
		}
		PatientDto patientDto = patientResponse.getBody();

		AppointmentDto responseDto = modelMapper.map(updatedAppointment, AppointmentDto.class);
		responseDto.setDoctor(doctorDto);
		responseDto.setPatient(patientDto);

		return responseDto;
	}

 
	@Override
	public void deleteAppointmentData(Integer appointmentId) {
		Appointment appointment = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
		
		appointmentRepository.delete(appointment);

	}

}
