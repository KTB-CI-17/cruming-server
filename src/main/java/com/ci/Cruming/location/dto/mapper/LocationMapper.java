package com.ci.Cruming.location.dto.mapper;

import com.ci.Cruming.location.dto.LocationRequest;
import com.ci.Cruming.location.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location toLocation(LocationRequest locationRequest) {
        return Location.builder()
                .placeName(locationRequest.placeName())
                .address(locationRequest.address())
                .latitude(locationRequest.latitude())
                .longitude(locationRequest.longitude())
                .build();
    }
}
