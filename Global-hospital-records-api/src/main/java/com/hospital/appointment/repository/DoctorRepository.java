package com.hospital.appointment.repository;

import com.hospital.appointment.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findBySpecializationIgnoreCase(String specialization);
    List<Doctor> findByActiveTrue();
    boolean existsByHospitalId(Long hospitalId);

    // The core "find a doctor near me" query: all filters are optional so
    // callers can search by country alone, country + state/province, add a
    // specialization filter, or any combination. Only active doctors at
    // active hospitals are returned.
    @Query("SELECT d FROM Doctor d WHERE d.active = true "
            + "AND d.hospital.active = true "
            + "AND (:countryId IS NULL OR d.hospital.country.id = :countryId) "
            + "AND (:stateProvinceId IS NULL OR d.hospital.stateOrProvince.id = :stateProvinceId) "
            + "AND (:hospitalId IS NULL OR d.hospital.id = :hospitalId) "
            + "AND (:specialization IS NULL OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :specialization, '%')))")
    List<Doctor> search(@Param("countryId") Long countryId,
                         @Param("stateProvinceId") Long stateProvinceId,
                         @Param("hospitalId") Long hospitalId,
                         @Param("specialization") String specialization);
}
