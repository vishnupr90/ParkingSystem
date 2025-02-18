package com.infy.parkingSystem.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.infy.parkingSystem.model.StreetPricing;
import com.infy.parkingSystem.repository.StreetPricingRepository;

@Component
public class StreetPricingCmdRunner implements CommandLineRunner {

	@Autowired
	private StreetPricingRepository streetPricingRepository;

	@Override
	public void run(String... args) throws Exception {

		List<StreetPricing> streetPricings = streetPricingRepository.findAll();
		streetPricings.forEach(price -> System.out.println(price.toString()));
	}

}
