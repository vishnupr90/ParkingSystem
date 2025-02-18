package com.infy.parkingSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.infy.parkingSystem.model.ParkingSession;
import com.infy.parkingSystem.model.StreetPricing;
import com.infy.parkingSystem.repository.ParkingSessionRepository;
import com.infy.parkingSystem.repository.StreetPricingRepository;

@SpringBootTest
@ActiveProfiles("test")
class ParkingSessionServiceTest {

	@Mock
	private ParkingSessionRepository parkingSessionRepository;

	@Mock
	private StreetPricingRepository streetPricingRepository;

	@InjectMocks
	private ParkingSessionService parkingSessionService;

	static String LICENSE_PLATE = "AB 123456";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void startSession_ShouldCreateAndSaveParkingSession() {
		Long streetPricingId = 1L;
		StreetPricing streetPricing = new StreetPricing();
		streetPricing.setId(streetPricingId);
		streetPricing.setStreetName("Java");
		streetPricing.setMinuteRate(15);

		when(streetPricingRepository.findByStreetName("Java")).thenReturn(Optional.of(streetPricing));

		ParkingSession session = parkingSessionService.startSession(LICENSE_PLATE, "Java");

		assertNotNull(session);
		assertEquals(LICENSE_PLATE, session.getLicensePlate());
		assertEquals(streetPricing, session.getStreetPricing());
		assertNotNull(session.getStartTime());
		verify(parkingSessionRepository, times(1)).save(any(ParkingSession.class));
	}

	@Test
	void endSession_ShouldUpdateAndSaveParkingSession() {
		ParkingSession session = new ParkingSession();
		session.setLicensePlate(LICENSE_PLATE);
		session.setStartTime(LocalDateTime.now().minusMinutes(30));
		StreetPricing streetPricing = new StreetPricing();
		streetPricing.setMinuteRate(15);
		session.setStreetPricing(streetPricing);

		when(parkingSessionRepository.findByLicensePlateAndEndTimeIsNull(LICENSE_PLATE))
				.thenReturn(Optional.of(session));

		ParkingSession endedSession = parkingSessionService.endSession(LICENSE_PLATE);

		assertNotNull(endedSession);
		assertNotNull(endedSession.getEndTime());
		assertEquals(450, endedSession.getCost()); // 30 minutes * 15 cents
		verify(parkingSessionRepository, times(1)).save(session);
	}
}
