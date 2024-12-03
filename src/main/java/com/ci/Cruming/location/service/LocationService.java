package com.ci.Cruming.location.service;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.location.dto.LocationRequest;
import com.ci.Cruming.location.dto.mapper.LocationMapper;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    public Location getOrCreateLocation(LocationRequest request) {
        validateRequest(request);
        return locationRepository
                .findByPlaceNameAndAddress(request.placeName(), request.address())
                .orElseGet(() -> createLocation(request));
    }

    @Transactional
    protected Location createLocation(LocationRequest request) {
        return locationRepository.save(locationMapper.toLocation(request));
    }

    private void validateRequest(LocationRequest request) {
        if (request == null) {
            throw new CrumingException(ErrorCode.INVALID_LOCATION);
        }
        if (request.address() == null || request.address().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_LOCATION_ADDRESS);
        }
        if (request.placeName() == null || request.placeName().isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_LOCATION_PLACE_NAME);
        }
        if (request.longitude() == null || request.latitude() == null) {
            throw new CrumingException(ErrorCode.INVALID_LOCATION_COORDINATES);
        }
    }
}
