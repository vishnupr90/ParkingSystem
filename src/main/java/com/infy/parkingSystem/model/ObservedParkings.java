package com.infy.parkingSystem.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "observed_parkings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservedParkings {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String licensePlate;
	@NotNull(message = "Street Name is mandatory")
	@ManyToOne
	@JoinColumn(name = "street_pricing_id")
	private StreetPricing streetPricing;
	private LocalDateTime observedTime;
	private Boolean isFined;
}
