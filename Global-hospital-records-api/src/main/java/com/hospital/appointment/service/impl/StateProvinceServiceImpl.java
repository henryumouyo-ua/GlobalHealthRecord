package com.hospital.appointment.service.impl;

import com.hospital.appointment.dto.StateProvinceRequest;
import com.hospital.appointment.exception.InvalidRequestException;
import com.hospital.appointment.exception.ResourceNotFoundException;
import com.hospital.appointment.model.Country;
import com.hospital.appointment.model.StateProvince;
import com.hospital.appointment.repository.CountryRepository;
import com.hospital.appointment.repository.StateProvinceRepository;
import com.hospital.appointment.service.StateProvinceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateProvinceServiceImpl implements StateProvinceService {

    private final StateProvinceRepository stateProvinceRepository;
    private final CountryRepository countryRepository;

    public StateProvinceServiceImpl(StateProvinceRepository stateProvinceRepository,
                                     CountryRepository countryRepository) {
        this.stateProvinceRepository = stateProvinceRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public StateProvince createStateProvince(StateProvinceRequest request) {
        Country country = countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Country not found with id: " + request.getCountryId()));

        if (stateProvinceRepository.existsByCountryIdAndNameIgnoreCase(country.getId(), request.getName())) {
            throw new InvalidRequestException(
                    "A state/province named '" + request.getName() + "' already exists in " + country.getName());
        }

        StateProvince stateProvince = new StateProvince();
        stateProvince.setName(request.getName());
        stateProvince.setCode(request.getCode());
        stateProvince.setCountry(country);

        return stateProvinceRepository.save(stateProvince);
    }

    @Override
    public StateProvince getStateProvinceById(Long id) {
        return stateProvinceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("State/province not found with id: " + id));
    }

    @Override
    public List<StateProvince> getAllStatesProvinces() {
        return stateProvinceRepository.findAll();
    }

    @Override
    public List<StateProvince> getStatesProvincesByCountry(Long countryId) {
        if (!countryRepository.existsById(countryId)) {
            throw new ResourceNotFoundException("Country not found with id: " + countryId);
        }
        return stateProvinceRepository.findByCountryId(countryId);
    }
}
