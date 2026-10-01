package com.hospital.appointment.repository;

import com.hospital.appointment.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    List<Hospital> findByActiveTrue();

    boolean existsByCountryId(Long countryId);

    boolean existsByStateOrProvinceId(Long stateProvinceId);

    // Location-based search for patients finding a hospital in their own
    // country and, optionally, a narrower state/province. Both filters are
    // optional so this one query covers "just my country" and "my country
    // and my state/province" alike.
    @Query("SELECT h FROM Hospital h WHERE h.active = true "
            + "AND (:countryId IS NULL OR h.country.id = :countryId) "
            + "AND (:stateProvinceId IS NULL OR h.stateOrProvince.id = :stateProvinceId)")
    List<Hospital> search(@Param("countryId") Long countryId,
                           @Param("stateProvinceId") Long stateProvinceId);
}
