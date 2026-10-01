package com.hospital.appointment.controller;

import com.hospital.appointment.dto.DoctorRequest;
import com.hospital.appointment.model.Doctor;
import com.hospital.appointment.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<Doctor> createDoctor(@Valid @RequestBody DoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.createDoctor(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // Find a doctor by country, state/province, hospital, and/or
    // specialization — any combination, all filters optional. This is the
    // main "find a doctor near me" endpoint patients use before booking.
    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> searchDoctors(
            @RequestParam(required = false) Long countryId,
            @RequestParam(required = false) Long stateProvinceId,
            @RequestParam(required = false) Long hospitalId,
            @RequestParam(required = false) String specialization) {
        return ResponseEntity.ok(
                doctorService.searchDoctors(countryId, stateProvinceId, hospitalId, specialization));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
