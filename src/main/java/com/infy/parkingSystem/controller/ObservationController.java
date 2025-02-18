package com.infy.parkingSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.parkingSystem.dto.ObservedParkingDTO;
import com.infy.parkingSystem.repository.ObservedParkingsRepository;
import com.infy.parkingSystem.service.ProcessObservationService;

@RestController
@RequestMapping("/api/observations")
public class ObservationController {
	@Autowired
	private ObservedParkingsRepository observationRepository;
	@Autowired
	private ProcessObservationService observationService;

	@PostMapping("/bulk-upload")
	public ResponseEntity<String> bulkUploadObservations(@RequestBody List<ObservedParkingDTO> observations) {

		return ResponseEntity.ok(observationService.bulkUploadObservation(observations));
	}
}