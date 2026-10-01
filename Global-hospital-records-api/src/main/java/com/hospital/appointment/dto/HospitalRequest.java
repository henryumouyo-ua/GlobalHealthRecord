package com.hospital.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HospitalRequest {

    @NotBlank(message = "name is required")
    private String name;

    private String address;

    @NotNull(message = "countryId is required")
    private Long countryId;

    // Optional: see StateProvince's javadoc for why.
    private Long stateProvinceId;
}
