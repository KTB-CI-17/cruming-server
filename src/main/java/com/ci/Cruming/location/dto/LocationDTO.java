package com.ci.Cruming.location.dto;

import com.ci.Cruming.location.entity.Location;

import java.time.LocalDateTime;

public record LocationDTO(
        Long id,
        String placeName,
        String address,
        Double latitude,
        Double longitude,
        LocalDateTime createdAt) {

    public static LocationDTO of(String placeName, String address, Double latitude, Double longitude) {
        return new LocationDTO(null, placeName, address, latitude, longitude, null);
    }

    public Location toEntity() {
        return Location.builder()
                .id(id)
                .placeName(placeName)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .createdAt(createdAt)
                .build();
    }
}
