package com.hospital.appointment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PatientRequest {

    @NotBlank(message = "fullName is required")
    private String fullName;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @NotBlank(message = "phoneNumber is required")
    @Pattern(regexp = "^\\+?[0-9()\\-\\s]{7,20}$", message = "phoneNumber format is invalid")
    private String phoneNumber;

    @NotNull(message = "countryId is required")
    private Long countryId;

    // Optional: see StateProvince's javadoc for why.
    private Long stateProvinceId;
}
