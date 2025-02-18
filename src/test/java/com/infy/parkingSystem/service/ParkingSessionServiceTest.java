package com.infy.parkingSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
    private  StreetPricingRepository streetPricingRepository;

    @Autowired
    private ParkingSessionService parkingSessionService;


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
        //verify(parkingSessionRepository, times(1)).save(any(ParkingSession.class));
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

        //when(parkingSessionRepository.findByLicensePlateAndEndTimeIsNull(licensePlate)).thenReturn(Optional.of(session));

        ParkingSession endedSession = parkingSessionService.endSession(licensePlate);

        assertNotNull(endedSession);
        assertNotNull(endedSession.getEndTime());
        
    }
}