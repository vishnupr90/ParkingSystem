/**
 *
 */
package com.infy.parkingSystem.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.infy.parkingSystem.dto.ObservedParkingDTO;
import com.infy.parkingSystem.model.ObservedParkings;
import com.infy.parkingSystem.model.StreetPricing;
import com.infy.parkingSystem.repository.ObservedParkingsRepository;
import com.infy.parkingSystem.repository.ParkingSessionRepository;
import com.infy.parkingSystem.repository.StreetPricingRepository;

/**
 *
 */
@Service
public class ProcessObservationService {

    @Autowired
    private ObservedParkingsRepository observationRepository;
    @Autowired
    private ParkingSessionRepository parkingSessionRepository;
    @Autowired
    private StreetPricingRepository streetPricingRepository;

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    public void checkUnregisteredPlates() {
        var observations = observationRepository.findByIsFinedIsNull();
        for (ObservedParkings observation : observations) {
            boolean isRegistered = parkingSessionRepository
                    .findByLicensePlateAndEndTimeIsNull(observation.getLicensePlate()).isPresent();
            observation.setIsFined(!isRegistered);
            observationRepository.save(observation);
            if (!isRegistered) {
                generatePdfReport(observation.getLicensePlate(), observation.getStreetPricing().getStreetName(), observation.getObservedTime());
            }
        }
    }

    public String bulkUploadObservation(List<ObservedParkingDTO> observations) {
        var observationEntities = observations.stream().map(dto -> {
            ObservedParkings observation = new ObservedParkings();
            observation.setLicensePlate(dto.licensePlate());
            StreetPricing streetPricing = streetPricingRepository.findByStreetName(dto.streetName())
                    .orElseThrow(() -> new RuntimeException("Street not found"));
            observation.setStreetPricing(streetPricing);
            observation.setObservedTime(dto.observationTime());
            observation.setIsFined(null); // Initialize isFined as null to identify if the record is processed or not
            return observation;
        }).collect(Collectors.toList());

        observationRepository.saveAll(observationEntities);
        return "Observations uploaded successfully";
    }

    private void generatePdfReport(String licensePlate, String streetName, LocalDateTime observationDate) {
        var fileName = "report_" + licensePlate + ".pdf";
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Unregistered License Plate Report");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("License Plate: " + licensePlate);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Street Name: " + streetName);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Observation Date: " + observationDate);
				contentStream.newLineAtOffset(0, -20);
				contentStream.showText("Fine Amount: €200");
                contentStream.endText();
            }

            document.save(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
