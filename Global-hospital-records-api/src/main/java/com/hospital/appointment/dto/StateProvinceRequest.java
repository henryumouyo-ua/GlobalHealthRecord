package com.hospital.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StateProvinceRequest {

    @NotBlank(message = "name is required")
    private String name;

    private String code;

    @NotNull(message = "countryId is required")
    private Long countryId;
}
