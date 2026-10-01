package com.hospital.appointment.service;

import com.hospital.appointment.model.Country;

import java.util.List;

public interface CountryService {
    Country createCountry(Country country);
    Country getCountryById(Long id);
    List<Country> getAllCountries();
}
