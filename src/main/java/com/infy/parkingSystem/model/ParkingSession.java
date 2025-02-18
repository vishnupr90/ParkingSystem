package com.infy.parkingSystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSession {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "License plate is mandatory")
	//@Pattern(regexp = "^[A-Z]{2} \\d{1,6}$", message = "License plate must be in following format: ZH 123456")
	private String licensePlate;

	@NotNull(message = "Street Name is mandatory")
    @ManyToOne
    @JoinColumn(name = "street_pricing_id")
    private StreetPricing streetPricing;

	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private int cost;
}