package com.hospital.appointment.service.impl;

import com.hospital.appointment.exception.ResourceNotFoundException;
import com.hospital.appointment.model.Country;
import com.hospital.appointment.repository.CountryRepository;
import com.hospital.appointment.service.CountryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Country createCountry(Country country) {
        country.setIsoCode(country.getIsoCode().toUpperCase());
        return countryRepository.save(country);
    }

    @Override
    public Country getCountryById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found with id: " + id));
    }

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }
}
