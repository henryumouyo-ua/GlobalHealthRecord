package com.hospital.appointment.service.impl;

import com.hospital.appointment.dto.HospitalRequest;
import com.hospital.appointment.exception.InvalidRequestException;
import com.hospital.appointment.exception.ResourceNotFoundException;
import com.hospital.appointment.model.Country;
import com.hospital.appointment.model.Hospital;
import com.hospital.appointment.model.StateProvince;
import com.hospital.appointment.repository.CountryRepository;
import com.hospital.appointment.repository.HospitalRepository;
import com.hospital.appointment.repository.StateProvinceRepository;
import com.hospital.appointment.service.HospitalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;
    private final CountryRepository countryRepository;
    private final StateProvinceRepository stateProvinceRepository;

    public HospitalServiceImpl(HospitalRepository hospitalRepository,
                                CountryRepository countryRepository,
                                StateProvinceRepository stateProvinceRepository) {
        this.hospitalRepository = hospitalRepository;
        this.countryRepository = countryRepository;
        this.stateProvinceRepository = stateProvinceRepository;
    }

    @Override
    public Hospital createHospital(HospitalRequest request) {
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

        Hospital hospital = new Hospital();
        hospital.setName(request.getName());
        hospital.setAddress(request.getAddress());
        hospital.setCountry(country);
        hospital.setStateOrProvince(stateProvince);

        return hospitalRepository.save(hospital);
    }

    @Override
    public Hospital getHospitalById(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));
    }

    @Override
    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findByActiveTrue();
    }

    @Override
    public List<Hospital> searchHospitals(Long countryId, Long stateProvinceId) {
        return hospitalRepository.search(countryId, stateProvinceId);
    }

    @Override
    public void deleteHospital(Long id) {
        // Soft delete, same convention as Patient/Doctor: doctors and
        // appointment history tied to this hospital must survive.
        Hospital hospital = getHospitalById(id);
        hospital.setActive(false);
        hospitalRepository.save(hospital);
    }
}
