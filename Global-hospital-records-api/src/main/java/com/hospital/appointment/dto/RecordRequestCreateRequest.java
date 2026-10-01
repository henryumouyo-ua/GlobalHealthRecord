package com.hospital.appointment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecordRequestCreateRequest {

    @NotNull(message = "patientId is required")
    private Long patientId;

    @NotNull(message = "hospitalId is required")
    private Long hospitalId;

    // Optional: scope the request to one specific past appointment rather
    // than the patient's entire record at this hospital.
    private Long appointmentId;

    private String reason;
}
