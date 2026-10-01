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

/**
 * A state, province, region, or equivalent first-level administrative
 * division within a {@link Country}. Not every country uses this concept the
 * same way (and some callers may have none to offer), which is why
 * {@code stateOrProvince} is optional on {@link Hospital} and {@link Patient}
 * — only {@link Country} is mandatory for those.
 */
@Entity
@Table(name = "states_provinces", uniqueConstraints = {
        @UniqueConstraint(name = "uk_state_name_per_country", columnNames = {"country_id", "name"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StateProvince {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "State/province name is required")
    @Column(nullable = false)
    private String name;

    // Optional short code, e.g. "CA" for California, "ON" for Ontario.
    // Not unique globally (many countries reuse two-letter codes), only
    // meaningful in combination with its country.
    private String code;

    @NotNull(message = "Country is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToMany(mappedBy = "stateOrProvince", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<Hospital> hospitals = new ArrayList<>();

    @OneToMany(mappedBy = "stateOrProvince", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<Patient> patients = new ArrayList<>();
}
