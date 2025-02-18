/**
 * 
 */
package com.infy.parkingSystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infy.parkingSystem.model.ParkingSession;

/**
 * 
 */
@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {
	// Custom query methods if needed
	Optional<ParkingSession> findByLicensePlateAndEndTimeIsNull(String licensePlate);
}