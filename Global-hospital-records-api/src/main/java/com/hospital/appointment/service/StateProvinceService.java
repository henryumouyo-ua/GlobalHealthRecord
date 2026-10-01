package com.hospital.appointment.service;

import com.hospital.appointment.dto.StateProvinceRequest;
import com.hospital.appointment.model.StateProvince;

import java.util.List;

public interface StateProvinceService {
    StateProvince createStateProvince(StateProvinceRequest request);
    StateProvince getStateProvinceById(Long id);
    List<StateProvince> getAllStatesProvinces();
    List<StateProvince> getStatesProvincesByCountry(Long countryId);
}
