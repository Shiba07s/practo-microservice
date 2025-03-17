package com.appointment_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appointment_service.dtos.AppointmentDto;
import com.appointment_service.exception.ResourceNotFoundException;
import com.appointment_service.services.AppointmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/create")
    public ResponseEntity<AppointmentDto> createAppointment(@RequestBody AppointmentDto appointmentDto) {
        try {
            AppointmentDto createdAppointment = appointmentService.bookAppointment(appointmentDto);
            return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to book appointment: " + ex.getMessage());
        }
    }
    
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDto>getAppointmentDetailsById(@PathVariable int appointmentId){
    	AppointmentDto appointment = appointmentService.getAppointment(appointmentId);
		return new ResponseEntity<>(appointment,HttpStatus.OK);
    }
    
	@GetMapping
	public ResponseEntity<List<AppointmentDto>> getAllAppointmentDetails() {
		List<AppointmentDto> allAppointments = appointmentService.getAllAppointments();
		return new ResponseEntity<List<AppointmentDto>>(allAppointments, HttpStatus.OK);
	}
	
	@PutMapping("/update/{appointmentId}")
	public ResponseEntity<AppointmentDto> updateAppointmentDetails(@PathVariable Integer appointmentId,
			@RequestBody AppointmentDto appointmentDto) {
		AppointmentDto updateAppointment = appointmentService.updateAppointment(appointmentId, appointmentDto);
		return new ResponseEntity<>(updateAppointment, HttpStatus.CREATED);
	}

	@DeleteMapping("/delete/{appointmentId}")
	public ResponseEntity<String> deleteAppointmentDetails(@PathVariable Integer appointmentId) {
		appointmentService.deleteAppointmentData(appointmentId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}