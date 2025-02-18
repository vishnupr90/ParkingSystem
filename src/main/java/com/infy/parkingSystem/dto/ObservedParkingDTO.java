/**
 * 
 */
package com.infy.parkingSystem.dto;

import java.time.LocalDateTime;

/**
 * 
 */
public record ObservedParkingDTO(String licensePlate, String streetName, LocalDateTime observationTime) {
}
