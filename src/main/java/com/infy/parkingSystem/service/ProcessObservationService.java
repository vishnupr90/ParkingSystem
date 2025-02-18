/**
 * 
 */
package com.infy.parkingSystem.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.infy.parkingSystem.dto.ObservedParkingDTO;
import com.infy.parkingSystem.model.ObservedParkings;
import com.infy.parkingSystem.model.StreetPricing;
import com.infy.parkingSystem.repository.ObservedParkingsRepository;
import com.infy.parkingSystem.repository.ParkingSessionRepository;
import com.infy.parkingSystem.repository.StreetPricingRepository;

/**
 * 
 */
@Service
public class ProcessObservationService {

	@Autowired
	private ObservedParkingsRepository observationRepository;
	@Autowired
	private ParkingSessionRepository parkingSessionRepository;
	@Autowired
	private StreetPricingRepository streetPricingRepository;

	@Scheduled(cron = "0 0 * * * *") // Runs every hour
	public void checkUnregisteredPlates() {
		List<ObservedParkings> observations = observationRepository.findByIsFinedIsNull();
		for (ObservedParkings observation : observations) {
			boolean isRegistered = parkingSessionRepository
					.findByLicensePlateAndEndTimeIsNull(observation.getLicensePlate()).isPresent();
			if (!isRegistered) {
				observation.setIsFined(!isRegistered);
				observationRepository.save(observation);
				// trigger email notification
			}
		}
	}

	public String bulkUploadObservation(List<ObservedParkingDTO> observations) {
		List<ObservedParkings> observationEntities = observations.stream().map(dto -> {
			ObservedParkings observation = new ObservedParkings();
			observation.setLicensePlate(dto.licensePlate());
			StreetPricing streetPricing = streetPricingRepository.findByStreetName(dto.streetName())
					.orElseThrow(() -> new RuntimeException("Street not found"));
			observation.setStreetPricing(streetPricing);
			observation.setObservedTime(dto.observationTime());
			observation.setIsFined(null); // Initialize isFined as null to identify if the record is processed or not
			return observation;
		}).collect(Collectors.toList());

		observationRepository.saveAll(observationEntities);
		return "Observations uploaded successfully";
	}
}
