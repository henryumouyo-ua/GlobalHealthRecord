package com.hospital.appointment.controller;

import com.hospital.appointment.dto.StateProvinceRequest;
import com.hospital.appointment.model.StateProvince;
import com.hospital.appointment.service.StateProvinceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/states-provinces")
public class StateProvinceController {

    private final StateProvinceService stateProvinceService;

    public StateProvinceController(StateProvinceService stateProvinceService) {
        this.stateProvinceService = stateProvinceService;
    }

    @PostMapping
    public ResponseEntity<StateProvince> createStateProvince(@Valid @RequestBody StateProvinceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stateProvinceService.createStateProvince(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StateProvince> getStateProvince(@PathVariable Long id) {
        return ResponseEntity.ok(stateProvinceService.getStateProvinceById(id));
    }

    // Omit countryId to list every state/province; pass it to scope to one country.
    @GetMapping
    public ResponseEntity<List<StateProvince>> getStatesProvinces(
            @RequestParam(required = false) Long countryId) {
        List<StateProvince> result = (countryId != null)
                ? stateProvinceService.getStatesProvincesByCountry(countryId)
                : stateProvinceService.getAllStatesProvinces();
        return ResponseEntity.ok(result);
    }
}
