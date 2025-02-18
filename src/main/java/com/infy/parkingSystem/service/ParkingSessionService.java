package com.infy.parkingSystem.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infy.parkingSystem.model.ObservedParkings;
import com.infy.parkingSystem.model.ParkingSession;
import com.infy.parkingSystem.model.StreetPricing;
import com.infy.parkingSystem.repository.ParkingSessionRepository;
import com.infy.parkingSystem.repository.StreetPricingRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSessionService {
	@Autowired
	private final ParkingSessionRepository repository;
	@Autowired
	private final StreetPricingRepository streetPricingRepository;

	@Transactional
	public ParkingSession startSession(String licensePlate, String streetName) {
		
		//validating if there is any active session
		if (repository.findByLicensePlateAndEndTimeIsNull(licensePlate).isPresent()) {
            throw new RuntimeException("A parking session with this license plate is already active.");
        }
		var session = new ParkingSession();
		System.out.println("inside start session");
		StreetPricing streetPricing = streetPricingRepository.findByStreetName(streetName)
				.orElseThrow(() -> new RuntimeException("Street not found"));
		System.out.println("Street found:" + streetPricing.getId());
		session.setLicensePlate(licensePlate);
		session.setStreetPricing(streetPricing);
		session.setStartTime(LocalDateTime.now());
		return repository.save(session);
	}

	@Transactional
	public ParkingSession endSession(String licensePlate) {
		var session = repository.findByLicensePlateAndEndTimeIsNull(licensePlate)
				.orElseThrow(() -> new RuntimeException("Session not found"));
		session.setEndTime(LocalDateTime.now());
		session.setCost(calculateCost(session));
		return repository.save(session);
	}

	private int calculateCost(ParkingSession session) {
		// StreetPricing pricing =
		// streetPricingRepository.findByStreetName(session.getStreetName())
		// .orElseThrow(() -> new RuntimeException("Street pricing not found"));

		var duration = Duration.between(session.getStartTime(), session.getEndTime());
		var minutes = duration.toMinutes();
		return (int) (minutes * session.getStreetPricing().getMinuteRate());
	}

	public List<ParkingSession> getAllSessions() {
		return repository.findAll();
	}
}