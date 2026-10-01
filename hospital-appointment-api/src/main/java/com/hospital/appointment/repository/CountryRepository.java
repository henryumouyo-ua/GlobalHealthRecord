package com.hospital.appointment.repository;

import com.hospital.appointment.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByIsoCodeIgnoreCase(String isoCode);
    Optional<Country> findByNameIgnoreCase(String name);
    boolean existsByIsoCodeIgnoreCase(String isoCode);
}
