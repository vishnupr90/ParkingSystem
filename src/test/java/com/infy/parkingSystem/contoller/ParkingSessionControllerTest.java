package com.infy.parkingSystem.contoller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.infy.parkingSystem.controller.ParkingSessionController;
import com.infy.parkingSystem.model.ParkingSession;
import com.infy.parkingSystem.model.StreetPricing;
import com.infy.parkingSystem.service.ParkingSessionService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class ParkingSessionControllerTest {

	@Mock
	private ParkingSessionService parkingSessionService;

	@InjectMocks
	private ParkingSessionController parkingSessionController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(parkingSessionController).build();
	}

	@Test
	void startSession_ShouldReturnCreatedSession() throws Exception {
		String licensePlate = "ABC1234";
		String streetName = "Java";
		StreetPricing streetPricing = new StreetPricing();
		// streetPricing.setId(streetPricingId);
		streetPricing.setStreetName("Java");
		streetPricing.setMinuteRate(15);
		ParkingSession session = new ParkingSession();
		session.setId(1L);
		session.setLicensePlate(licensePlate);
		session.setStreetPricing(streetPricing);
		session.setStartTime(LocalDateTime.now());

		when(parkingSessionService.startSession(licensePlate, streetName)).thenReturn(session);

		mockMvc.perform(post("/api/parking/start").param("licensePlate", licensePlate)
				.param("streetName", streetName).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void endSession_ShouldReturnEndedSession() throws Exception {
		String licensePlate = "ABC1234";
		StreetPricing streetPricing = new StreetPricing();
		streetPricing.setId(1L);
		streetPricing.setStreetName("Java");
		streetPricing.setMinuteRate(15);
		ParkingSession session = new ParkingSession();
		session.setId(1L);
		session.setLicensePlate(licensePlate);
		session.setStreetPricing(streetPricing);
		session.setStartTime(LocalDateTime.now().minusMinutes(30));
		session.setEndTime(LocalDateTime.now());
		session.setCost(450);

		when(parkingSessionService.endSession(licensePlate)).thenReturn(session);

		mockMvc.perform(
				post("/api/parking/end").param("licensePlate", licensePlate).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void getAllSessions_ShouldReturnListOfSessions() throws Exception {
		StreetPricing streetPricing = new StreetPricing();
		streetPricing.setId(1L);
		streetPricing.setStreetName("Java");
		streetPricing.setMinuteRate(15);
		ParkingSession session = new ParkingSession();
		session.setId(1L);
		session.setLicensePlate("ABC1234");
		session.setStreetPricing(streetPricing);
		session.setStartTime(LocalDateTime.now().minusMinutes(30));
		session.setEndTime(LocalDateTime.now());
		session.setCost(450);

		when(parkingSessionService.getAllSessions()).thenReturn(Collections.singletonList(session));

		mockMvc.perform(get("/api/parking/sessions").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
}