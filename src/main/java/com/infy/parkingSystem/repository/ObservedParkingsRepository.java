package com.infy.parkingSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infy.parkingSystem.model.ObservedParkings;

public interface ObservedParkingsRepository extends JpaRepository<ObservedParkings, Long> {
	List<ObservedParkings> findByIsFinedIsNull();
}