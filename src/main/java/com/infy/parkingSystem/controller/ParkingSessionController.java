/**
 * 
 */
package com.infy.parkingSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infy.parkingSystem.model.ParkingSession;
import com.infy.parkingSystem.service.ParkingSessionService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
@Validated
public class ParkingSessionController {
	@Autowired
	private final ParkingSessionService service;

	@PostMapping("/start")
	public ResponseEntity<ParkingSession> startSession(
			@RequestParam @NotBlank(message = "License plate is mandatory") 
			@Pattern(regexp = "^[a-zA-Z0-9]{4,8}$", message = "Invalid license plate format")
			String licensePlate,
			@RequestParam @NotBlank(message = "Street name is mandatory") String streetName) {
		return ResponseEntity.ok(service.startSession(licensePlate, streetName));
	}

	@PostMapping("/end")
	public ResponseEntity<ParkingSession> endSession(
			@RequestParam @NotBlank(message = "License plate is mandatory")
			@Pattern(regexp = "^[a-zA-Z0-9]{4,8}$", message = "Invalid license plate format")
			String licensePlate) {
		return ResponseEntity.ok(service.endSession(licensePlate));
	}

	@GetMapping("/sessions")
	public ResponseEntity<List<ParkingSession>> getAllSessions() {
		return ResponseEntity.ok(service.getAllSessions());
	}

}