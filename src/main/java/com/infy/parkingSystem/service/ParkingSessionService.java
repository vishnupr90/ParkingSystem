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

import java.time.DayOfWeek;
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

		// validating if there is any active session
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

		// no charges for parking within a Sunday
		if (session.getStartTime().getDayOfWeek() == DayOfWeek.SUNDAY
				&& session.getEndTime().getDayOfWeek() == DayOfWeek.SUNDAY
				&& session.getStartTime().toLocalDate().equals(session.getEndTime().toLocalDate())) {
			return 0;

		}

		var duration = Duration.between(session.getStartTime(), session.getEndTime());
		var totalMinutes = duration.toMinutes();

		// calculating non-payable minutes
		var nonPayableMinutes = calculateNonPayableMinutes(session.getStartTime(), session.getEndTime());
		var payableMinutes = totalMinutes - nonPayableMinutes;

		return (int) (payableMinutes * session.getStreetPricing().getMinuteRate());
	}

	//calcuation for cars parked for several days
	private long calculateNonPayableMinutes(LocalDateTime startTime, LocalDateTime endTime) {
		long nonPayableMinutes = 0;
		LocalDateTime currentTime = startTime;

		// checking if the duration contains Sunday or hours between 21:00 and 8:00
		while (currentTime.isBefore(endTime)) {
			if (currentTime.getDayOfWeek().getValue() == 7 || currentTime.getHour() < 8
					|| currentTime.getHour() >= 21) {
				nonPayableMinutes++;
			}
			currentTime = currentTime.plusMinutes(1);
		}
		return nonPayableMinutes;
	}

	public List<ParkingSession> getAllSessions() {
		return repository.findAll();
	}
}