package com.hospital.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DoctorRequest {

    @NotBlank(message = "fullName is required")
    private String fullName;

    @NotBlank(message = "specialization is required")
    private String specialization;

    @NotNull(message = "hospitalId is required")
    private Long hospitalId;
}
