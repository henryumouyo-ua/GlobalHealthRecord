package com.hospital.appointment.service;

import com.hospital.appointment.dto.DoctorRequest;
import com.hospital.appointment.model.Doctor;

import java.util.List;

public interface DoctorService {
    Doctor createDoctor(DoctorRequest request);
    Doctor getDoctorById(Long id);
    List<Doctor> getAllDoctors();
    List<Doctor> searchDoctors(Long countryId, Long stateProvinceId, Long hospitalId, String specialization);
    void deleteDoctor(Long id);
}
