package com.hospital.appointment.service;

import com.hospital.appointment.dto.RecordRequestCreateRequest;
import com.hospital.appointment.dto.RecordRequestDecisionRequest;
import com.hospital.appointment.dto.RecordRequestResponse;

import java.util.List;

public interface HospitalRecordRequestService {

    RecordRequestResponse createRequest(RecordRequestCreateRequest request);

    RecordRequestResponse cancelRequest(Long requestId);

    RecordRequestResponse approveRequest(Long requestId, RecordRequestDecisionRequest decision);

    RecordRequestResponse rejectRequest(Long requestId, RecordRequestDecisionRequest decision);

    RecordRequestResponse fulfillRequest(Long requestId, RecordRequestDecisionRequest decision);

    RecordRequestResponse getRequestById(Long requestId);

    List<RecordRequestResponse> getAllRequests();

    List<RecordRequestResponse> getRequestsByPatient(Long patientId);

    List<RecordRequestResponse> getRequestsByHospital(Long hospitalId);
}
