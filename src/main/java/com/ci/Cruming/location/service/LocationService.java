package com.ci.Cruming.location.service;

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

    @Transactional
    public Location getOrCreateLocation(LocationRequest request) {
        return locationRepository
                .findByPlaceNameAndAddress(request.placeName(), request.address())
                .orElseGet(() -> {
                    Location location = locationMapper.toLocation(request);
                    return locationRepository.save(location);
                });
    }
}
