package com.infy.parkingSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.parkingSystem.model.StreetPricing;
import com.infy.parkingSystem.repository.StreetPricingRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/street")
@RequiredArgsConstructor
@Validated
public class StreetPricingController {

	@Autowired
	private StreetPricingRepository streetPricingRepository;

	@GetMapping("/streetPrices")
	public ResponseEntity<List<StreetPricing>> getAllStreetPrices() {
		return ResponseEntity.ok(streetPricingRepository.findAll());
	}

}
