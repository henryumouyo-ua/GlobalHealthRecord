package com.hospital.appointment.repository;

import com.hospital.appointment.model.HospitalRecordRequest;
import com.hospital.appointment.model.RecordRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospitalRecordRequestRepository extends JpaRepository<HospitalRecordRequest, Long> {
    List<HospitalRecordRequest> findByPatientId(Long patientId);
    List<HospitalRecordRequest> findByHospitalId(Long hospitalId);
    List<HospitalRecordRequest> findByStatus(RecordRequestStatus status);
}
