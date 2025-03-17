package com.doctor_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DoctorStatus {
    VERIFIED("Verified"),
    UNVERIFIED("Unverified");
	
    private final String displayName;

}