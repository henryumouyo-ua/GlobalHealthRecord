package com.hospital.appointment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "countries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Country name is required")
    @Column(nullable = false, unique = true)
    private String name;

    // ISO 3166-1 alpha-2 or alpha-3 code, e.g. "US", "NG", "GBR".
    // Stored uppercase; used as a stable short identifier independent of
    // how the country's display name is spelled/localized.
    @NotBlank(message = "ISO code is required")
    @Pattern(regexp = "^[A-Z]{2,3}$", message = "isoCode must be 2-3 uppercase letters (ISO 3166-1)")
    @Column(nullable = false, unique = true, length = 3)
    private String isoCode;

    @OneToMany(mappedBy = "country", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<StateProvince> statesOrProvinces = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<Hospital> hospitals = new ArrayList<>();

    @OneToMany(mappedBy = "country", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<Patient> patients = new ArrayList<>();
}
