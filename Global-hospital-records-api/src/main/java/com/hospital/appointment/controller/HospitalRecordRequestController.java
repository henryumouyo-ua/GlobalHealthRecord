package com.hospital.appointment.controller;

import com.hospital.appointment.dto.RecordRequestCreateRequest;
import com.hospital.appointment.dto.RecordRequestDecisionRequest;
import com.hospital.appointment.dto.RecordRequestResponse;
import com.hospital.appointment.service.HospitalRecordRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/record-requests")
public class HospitalRecordRequestController {

    private final HospitalRecordRequestService recordRequestService;

    public HospitalRecordRequestController(HospitalRecordRequestService recordRequestService) {
        this.recordRequestService = recordRequestService;
    }

    // Patient files a request for their own hospital records, optionally
    // scoped to one specific past appointment.
    @PostMapping
    public ResponseEntity<RecordRequestResponse> createRequest(
            @Valid @RequestBody RecordRequestCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordRequestService.createRequest(request));
    }

    // Patient withdraws their own request (while it's still PENDING or APPROVED).
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<RecordRequestResponse> cancelRequest(@PathVariable Long id) {
        return ResponseEntity.ok(recordRequestService.cancelRequest(id));
    }

    // The following three endpoints represent hospital-staff actions. There's
    // no authentication/authorization layer yet (see README), so nothing here
    // currently restricts who can call them — see docs/DATA_MODEL.md.
    @PatchMapping("/{id}/approve")
    public ResponseEntity<RecordRequestResponse> approveRequest(
            @PathVariable Long id,
            @RequestBody(required = false) RecordRequestDecisionRequest decision) {
        return ResponseEntity.ok(recordRequestService.approveRequest(id, decision));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<RecordRequestResponse> rejectRequest(
            @PathVariable Long id,
            @RequestBody(required = false) RecordRequestDecisionRequest decision) {
        return ResponseEntity.ok(recordRequestService.rejectRequest(id, decision));
    }

    @PatchMapping("/{id}/fulfill")
    public ResponseEntity<RecordRequestResponse> fulfillRequest(
            @PathVariable Long id,
            @RequestBody(required = false) RecordRequestDecisionRequest decision) {
        return ResponseEntity.ok(recordRequestService.fulfillRequest(id, decision));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordRequestResponse> getRequest(@PathVariable Long id) {
        return ResponseEntity.ok(recordRequestService.getRequestById(id));
    }

    @GetMapping
    public ResponseEntity<List<RecordRequestResponse>> getAllRequests() {
        return ResponseEntity.ok(recordRequestService.getAllRequests());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<RecordRequestResponse>> getRequestsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(recordRequestService.getRequestsByPatient(patientId));
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<RecordRequestResponse>> getRequestsByHospital(@PathVariable Long hospitalId) {
        return ResponseEntity.ok(recordRequestService.getRequestsByHospital(hospitalId));
    }
}
