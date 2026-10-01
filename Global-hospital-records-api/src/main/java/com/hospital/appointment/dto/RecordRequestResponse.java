package com.hospital.appointment.dto;

import com.hospital.appointment.model.HospitalRecordRequest;
import com.hospital.appointment.model.RecordRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordRequestResponse {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long hospitalId;
    private String hospitalName;
    private Long appointmentId;
    private String reason;
    private RecordRequestStatus status;
    private String staffNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RecordRequestResponse fromEntity(HospitalRecordRequest request) {
        return new RecordRequestResponse(
                request.getId(),
                request.getPatient().getId(),
                request.getPatient().getFullName(),
                request.getHospital().getId(),
                request.getHospital().getName(),
                request.getAppointment() != null ? request.getAppointment().getId() : null,
                request.getReason(),
                request.getStatus(),
                request.getStaffNotes(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
