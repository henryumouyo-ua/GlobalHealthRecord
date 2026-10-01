package com.hospital.appointment.service.impl;

import com.hospital.appointment.dto.PatientRequest;
import com.hospital.appointment.exception.InvalidRequestException;
import com.hospital.appointment.exception.ResourceNotFoundException;
import com.hospital.appointment.model.Country;
import com.hospital.appointment.model.Patient;
import com.hospital.appointment.model.StateProvince;
import com.hospital.appointment.repository.CountryRepository;
import com.hospital.appointment.repository.PatientRepository;
import com.hospital.appointment.repository.StateProvinceRepository;
import com.hospital.appointment.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final CountryRepository countryRepository;
    private final StateProvinceRepository stateProvinceRepository;

    public PatientServiceImpl(PatientRepository patientRepository,
                               CountryRepository countryRepository,
                               StateProvinceRepository stateProvinceRepository) {
        this.patientRepository = patientRepository;
        this.countryRepository = countryRepository;
        this.stateProvinceRepository = stateProvinceRepository;
    }

    @Override
    public Patient createPatient(PatientRequest request) {
        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Country not found with id: " + request.getCountryId()));

        StateProvince stateProvince = null;
        if (request.getStateProvinceId() != null) {
            stateProvince = stateProvinceRepository.findById(request.getStateProvinceId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "State/province not found with id: " + request.getStateProvinceId()));

            if (!stateProvince.getCountry().getId().equals(country.getId())) {
                throw new InvalidRequestException(
                        "State/province " + stateProvince.getId() + " does not belong to country " + country.getId());
            }
        }

        Patient patient = new Patient();
        patient.setFullName(request.getFullName());
        patient.setEmail(request.getEmail());
        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setCountry(country);
        patient.setStateOrProvince(stateProvince);

        return patientRepository.save(patient);
    }

    @Override
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findByActiveTrue();
    }

    @Override
    public void deletePatient(Long id) {
        // Soft delete: appointment history referencing this patient must
        // remain intact, so we flip the active flag rather than removing
        // the row (see the data model doc, section 5.2).
        Patient patient = getPatientById(id);
        patient.setActive(false);
        patientRepository.save(patient);
    }
}
