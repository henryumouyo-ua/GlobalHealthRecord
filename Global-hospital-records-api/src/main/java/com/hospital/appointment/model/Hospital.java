package com.hospital.appointment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hospitals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Hospital name is required")
    @Column(nullable = false)
    private String name;

    private String address;

    @NotNull(message = "Country is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    // Optional: some countries aren't meaningfully subdivided for this
    // purpose, so a hospital can be located by country alone.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_province_id")
    private StateProvince stateOrProvince;

    // Soft-delete flag, same convention as Patient/Doctor: deactivating a
    // hospital must not destroy the doctors or appointment history tied to it.
    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "hospital", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<Doctor> doctors = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<Appointment> appointments = new ArrayList<>();
}
