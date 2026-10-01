package com.hospital.appointment.repository;

import com.hospital.appointment.model.StateProvince;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateProvinceRepository extends JpaRepository<StateProvince, Long> {
    List<StateProvince> findByCountryId(Long countryId);
    boolean existsByCountryIdAndNameIgnoreCase(Long countryId, String name);
}
