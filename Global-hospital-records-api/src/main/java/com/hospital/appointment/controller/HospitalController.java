package com.hospital.appointment.controller;

import com.hospital.appointment.dto.HospitalRequest;
import com.hospital.appointment.model.Hospital;
import com.hospital.appointment.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @PostMapping
    public ResponseEntity<Hospital> createHospital(@Valid @RequestBody HospitalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hospitalService.createHospital(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getHospital(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getHospitalById(id));
    }

    @GetMapping
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }

    // Find hospitals in a patient's own country and, optionally, a
    // narrower state/province. Both params are optional.
    @GetMapping("/search")
    public ResponseEntity<List<Hospital>> searchHospitals(
            @RequestParam(required = false) Long countryId,
            @RequestParam(required = false) Long stateProvinceId) {
        return ResponseEntity.ok(hospitalService.searchHospitals(countryId, stateProvinceId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        hospitalService.deleteHospital(id);
        return ResponseEntity.noContent().build();
    }
}
