package com.infy.parkingSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infy.parkingSystem.model.StreetPricing;

import java.util.Optional;

@Repository
public interface StreetPricingRepository extends JpaRepository<StreetPricing, Long> {
	Optional<StreetPricing> findByStreetName(String streetName);
}