package com.hospital.appointment.service;

import com.hospital.appointment.dto.HospitalRequest;
import com.hospital.appointment.model.Hospital;

import java.util.List;

public interface HospitalService {
    Hospital createHospital(HospitalRequest request);
    Hospital getHospitalById(Long id);
    List<Hospital> getAllHospitals();
    List<Hospital> searchHospitals(Long countryId, Long stateProvinceId);
    void deleteHospital(Long id);
}
