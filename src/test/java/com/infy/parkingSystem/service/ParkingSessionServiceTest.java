package com.infy.parkingSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.infy.parkingSystem.model.ParkingSession;
import com.infy.parkingSystem.model.StreetPricing;
import com.infy.parkingSystem.repository.ParkingSessionRepository;
import com.infy.parkingSystem.repository.StreetPricingRepository;

@SpringBootTest
@ActiveProfiles("test")
class ParkingSessionServiceTest {

	@Autowired
	private ParkingSessionRepository parkingSessionRepository;

	@Autowired
	private StreetPricingRepository streetPricingRepository;

	@Autowired
	private ParkingSessionService parkingSessionService;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	@Test
	void startSession_ShouldCreateAndSaveParkingSession() {
		String licensePlate = "ABC1234";
		Long streetPricingId = 1L;
		StreetPricing streetPricing = streetPricingRepository.findById(streetPricingId)
				.orElseThrow(() -> new RuntimeException("Street not found"));

		ParkingSession session = parkingSessionService.startSession(licensePlate, "Java");

		assertNotNull(session);
		assertEquals(licensePlate, session.getLicensePlate());
		assertEquals(streetPricing, session.getStreetPricing());
		assertNotNull(session.getStartTime());
		// verify(parkingSessionRepository, times(1)).save(any(ParkingSession.class));
	}

	@Test
	void endSession_ShouldUpdateAndSaveParkingSession() {
		String licensePlate = "ABC1234";
		ParkingSession session = new ParkingSession();
		session.setLicensePlate(licensePlate);
		session.setStartTime(LocalDateTime.now().minusMinutes(30));
		StreetPricing streetPricing = new StreetPricing();
		streetPricing.setMinuteRate(15);
		session.setStreetPricing(streetPricing);

		// when(parkingSessionRepository.findByLicensePlateAndEndTimeIsNull(licensePlate)).thenReturn(Optional.of(session));

		ParkingSession endedSession = parkingSessionService.endSession(licensePlate);

		assertNotNull(endedSession);
		assertNotNull(endedSession.getEndTime());

	}

	@Test
	public void testCalculateCost_WeekdayWithinChargeableHours() {

		LocalDateTime entryTime = LocalDateTime.parse("2025-02-18 09:00", formatter);
		LocalDateTime exitTime = LocalDateTime.parse("2025-02-18 17:00", formatter);
		String licensePlate = "ABC1234";
		ParkingSession session = new ParkingSession();
		session.setLicensePlate(licensePlate);
		session.setStartTime(entryTime);
		session.setEndTime(exitTime);
		StreetPricing streetPricing = new StreetPricing();
		streetPricing.setMinuteRate(15);
		session.setStreetPricing(streetPricing);

		double cost = parkingSessionService.calculateCost(session);
		System.out.println("cost is " + cost);
		assertEquals(7200.00, cost);
	}

	@Test
	public void testCalculateCost_WeekdaySpanningChargeableAndNonChargeableHours() {
		LocalDateTime entryTime = LocalDateTime.parse("2025-02-18 07:00", formatter);
		LocalDateTime exitTime = LocalDateTime.parse("2025-02-18 22:00", formatter);
		//parking durations are total:900 nonPayable:120 payable:780
		String licensePlate = "ABC1234";
		ParkingSession session = new ParkingSession();
		session.setLicensePlate(licensePlate);
		session.setStartTime(entryTime);
		session.setEndTime(exitTime);
		StreetPricing streetPricing = new StreetPricing();
		streetPricing.setMinuteRate(15);
		session.setStreetPricing(streetPricing);

		double cost = parkingSessionService.calculateCost(session);
		System.out.println("cost is " + cost);
		assertEquals(11700.00, cost);
	}

	@Test
	public void testCalculateCost_SundayNonChargeable() {
		LocalDateTime entryTime = LocalDateTime.parse("2025-02-16 10:00", formatter);
		LocalDateTime exitTime = LocalDateTime.parse("2025-02-16 18:00", formatter);
		// No charge
		String licensePlate = "ABC1234";
		ParkingSession session = new ParkingSession();
		session.setLicensePlate(licensePlate);
		session.setStartTime(entryTime);
		session.setEndTime(exitTime);
		StreetPricing streetPricing = new StreetPricing();
		streetPricing.setMinuteRate(15);
		session.setStreetPricing(streetPricing);

		double cost = parkingSessionService.calculateCost(session);
		System.out.println("cost is " + cost);
		assertEquals(0.00, cost);
	}

	@Test
	public void testCalculateCost_SpanningMultipleDaysIncludingSunday() {
		LocalDateTime entryTime = LocalDateTime.parse("2025-02-15 20:00", formatter);
		LocalDateTime exitTime = LocalDateTime.parse("2025-02-17 10:00", formatter);
		//parking durations are total:2280 nonPayable:2100 payable:180
		String licensePlate = "ABC1234";
		ParkingSession session = new ParkingSession();
		session.setLicensePlate(licensePlate);
		session.setStartTime(entryTime);
		session.setEndTime(exitTime);
		StreetPricing streetPricing = new StreetPricing();
		streetPricing.setMinuteRate(15);
		session.setStreetPricing(streetPricing);

		double cost = parkingSessionService.calculateCost(session);
		System.out.println("cost is " + cost);
		assertEquals(2700.00, cost);
	}

	@Test
	public void testCalculateCost_SpanningMultipleDaysExcludingSunday() {
		LocalDateTime entryTime = LocalDateTime.parse("2025-02-17 20:00", formatter);
		LocalDateTime exitTime = LocalDateTime.parse("2025-02-19 10:00", formatter);
		//parking durations are total:2280 nonPayable:1320 payable:960
		String licensePlate = "ABC1234";
		ParkingSession session = new ParkingSession();
		session.setLicensePlate(licensePlate);
		session.setStartTime(entryTime);
		session.setEndTime(exitTime);
		StreetPricing streetPricing = new StreetPricing();
		streetPricing.setMinuteRate(15);
		session.setStreetPricing(streetPricing);

		double cost = parkingSessionService.calculateCost(session);
		System.out.println("cost is " + cost);
		assertEquals(14400.00, cost);
	}
}