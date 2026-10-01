package com.hospital.appointment.service.impl;

import com.hospital.appointment.dto.RecordRequestCreateRequest;
import com.hospital.appointment.dto.RecordRequestDecisionRequest;
import com.hospital.appointment.dto.RecordRequestResponse;
import com.hospital.appointment.exception.InvalidRecordRequestException;
import com.hospital.appointment.exception.InvalidRequestException;
import com.hospital.appointment.exception.ResourceNotFoundException;
import com.hospital.appointment.model.Appointment;
import com.hospital.appointment.model.Hospital;
import com.hospital.appointment.model.HospitalRecordRequest;
import com.hospital.appointment.model.Patient;
import com.hospital.appointment.model.RecordRequestStatus;
import com.hospital.appointment.repository.AppointmentRepository;
import com.hospital.appointment.repository.HospitalRecordRequestRepository;
import com.hospital.appointment.repository.HospitalRepository;
import com.hospital.appointment.repository.PatientRepository;
import com.hospital.appointment.service.HospitalRecordRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HospitalRecordRequestServiceImpl implements HospitalRecordRequestService {

    private final HospitalRecordRequestRepository recordRequestRepository;
    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;
    private final AppointmentRepository appointmentRepository;

    public HospitalRecordRequestServiceImpl(HospitalRecordRequestRepository recordRequestRepository,
                                             PatientRepository patientRepository,
                                             HospitalRepository hospitalRepository,
                                             AppointmentRepository appointmentRepository) {
        this.recordRequestRepository = recordRequestRepository;
        this.patientRepository = patientRepository;
        this.hospitalRepository = hospitalRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional
    public RecordRequestResponse createRequest(RecordRequestCreateRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + request.getPatientId()));

        // Deliberately not checking hospital.isActive() here: a hospital
        // being deactivated in this system shouldn't block a patient from
        // requesting records of care they already received there.
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hospital not found with id: " + request.getHospitalId()));

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Appointment not found with id: " + request.getAppointmentId()));

            if (!appointment.getPatient().getId().equals(patient.getId())) {
                throw new InvalidRequestException(
                        "Appointment " + appointment.getId() + " does not belong to patient " + patient.getId());
            }
            if (!appointment.getHospital().getId().equals(hospital.getId())) {
                throw new InvalidRequestException(
                        "Appointment " + appointment.getId() + " was not at hospital " + hospital.getId());
            }
        }

        HospitalRecordRequest recordRequest = new HospitalRecordRequest();
        recordRequest.setPatient(patient);
        recordRequest.setHospital(hospital);
        recordRequest.setAppointment(appointment);
        recordRequest.setReason(request.getReason());
        recordRequest.setStatus(RecordRequestStatus.PENDING);

        HospitalRecordRequest saved = recordRequestRepository.save(recordRequest);
        return RecordRequestResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public RecordRequestResponse cancelRequest(Long requestId) {
        HospitalRecordRequest recordRequest = findRequestOrThrow(requestId);

        if (recordRequest.getStatus() != RecordRequestStatus.PENDING
                && recordRequest.getStatus() != RecordRequestStatus.APPROVED) {
            throw new InvalidRecordRequestException(
                    "Cannot cancel a request that is already " + recordRequest.getStatus());
        }

        recordRequest.setStatus(RecordRequestStatus.CANCELLED);
        HospitalRecordRequest saved = recordRequestRepository.save(recordRequest);
        return RecordRequestResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public RecordRequestResponse approveRequest(Long requestId, RecordRequestDecisionRequest decision) {
        HospitalRecordRequest recordRequest = findRequestOrThrow(requestId);

        if (recordRequest.getStatus() != RecordRequestStatus.PENDING) {
            throw new InvalidRecordRequestException(
                    "Only a PENDING request can be approved; this one is " + recordRequest.getStatus());
        }

        recordRequest.setStatus(RecordRequestStatus.APPROVED);
        if (decision != null) {
            recordRequest.setStaffNotes(decision.getStaffNotes());
        }
        HospitalRecordRequest saved = recordRequestRepository.save(recordRequest);
        return RecordRequestResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public RecordRequestResponse rejectRequest(Long requestId, RecordRequestDecisionRequest decision) {
        HospitalRecordRequest recordRequest = findRequestOrThrow(requestId);

        if (recordRequest.getStatus() != RecordRequestStatus.PENDING) {
            throw new InvalidRecordRequestException(
                    "Only a PENDING request can be rejected; this one is " + recordRequest.getStatus());
        }
        if (decision == null || decision.getStaffNotes() == null || decision.getStaffNotes().isBlank()) {
            throw new InvalidRequestException("staffNotes explaining the rejection is required");
        }

        recordRequest.setStatus(RecordRequestStatus.REJECTED);
        recordRequest.setStaffNotes(decision.getStaffNotes());
        HospitalRecordRequest saved = recordRequestRepository.save(recordRequest);
        return RecordRequestResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public RecordRequestResponse fulfillRequest(Long requestId, RecordRequestDecisionRequest decision) {
        HospitalRecordRequest recordRequest = findRequestOrThrow(requestId);

        if (recordRequest.getStatus() != RecordRequestStatus.APPROVED) {
            throw new InvalidRecordRequestException(
                    "Only an APPROVED request can be fulfilled; this one is " + recordRequest.getStatus());
        }

        recordRequest.setStatus(RecordRequestStatus.FULFILLED);
        if (decision != null && decision.getStaffNotes() != null) {
            recordRequest.setStaffNotes(decision.getStaffNotes());
        }
        HospitalRecordRequest saved = recordRequestRepository.save(recordRequest);
        return RecordRequestResponse.fromEntity(saved);
    }

    @Override
    public RecordRequestResponse getRequestById(Long requestId) {
        return RecordRequestResponse.fromEntity(findRequestOrThrow(requestId));
    }

    @Override
    public List<RecordRequestResponse> getAllRequests() {
        return recordRequestRepository.findAll().stream()
                .map(RecordRequestResponse::fromEntity)
                .toList();
    }

    @Override
    public List<RecordRequestResponse> getRequestsByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        return recordRequestRepository.findByPatientId(patientId).stream()
                .map(RecordRequestResponse::fromEntity)
                .toList();
    }

    @Override
    public List<RecordRequestResponse> getRequestsByHospital(Long hospitalId) {
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException("Hospital not found with id: " + hospitalId);
        }
        return recordRequestRepository.findByHospitalId(hospitalId).stream()
                .map(RecordRequestResponse::fromEntity)
                .toList();
    }

    private HospitalRecordRequest findRequestOrThrow(Long requestId) {
        return recordRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Record request not found with id: " + requestId));
    }
}
