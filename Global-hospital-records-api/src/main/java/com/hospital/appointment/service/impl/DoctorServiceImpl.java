package com.hospital.appointment.service.impl;

import com.hospital.appointment.dto.DoctorRequest;
import com.hospital.appointment.exception.InvalidRequestException;
import com.hospital.appointment.exception.ResourceNotFoundException;
import com.hospital.appointment.model.Doctor;
import com.hospital.appointment.model.Hospital;
import com.hospital.appointment.repository.DoctorRepository;
import com.hospital.appointment.repository.HospitalRepository;
import com.hospital.appointment.service.DoctorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;

    public DoctorServiceImpl(DoctorRepository doctorRepository, HospitalRepository hospitalRepository) {
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @Override
    public Doctor createDoctor(DoctorRequest request) {
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hospital not found with id: " + request.getHospitalId()));

        if (!hospital.isActive()) {
            throw new InvalidRequestException(
                    "Hospital with id " + hospital.getId() + " is not active");
        }

        Doctor doctor = new Doctor();
        doctor.setFullName(request.getFullName());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setHospital(hospital);

        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findByActiveTrue();
    }

    @Override
    public List<Doctor> searchDoctors(Long countryId, Long stateProvinceId, Long hospitalId, String specialization) {
        return doctorRepository.search(countryId, stateProvinceId, hospitalId, specialization);
    }

    @Override
    public void deleteDoctor(Long id) {
        // Soft delete: appointment history referencing this doctor must
        // remain intact, so we flip the active flag rather than removing
        // the row (see the data model doc, section 5.2).
        Doctor doctor = getDoctorById(id);
        doctor.setActive(false);
        doctorRepository.save(doctor);
    }
}
