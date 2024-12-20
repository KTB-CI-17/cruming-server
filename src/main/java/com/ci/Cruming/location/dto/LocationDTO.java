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

    public LocationDTO(String placeName, String address, Double latitude, Double longitude) {
        this(null, placeName, address, latitude, longitude, null);
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

    public static LocationDTO fromEntity(Location entity) {
        if (entity == null) return null;
        
        return new LocationDTO(
            entity.getId(),
            entity.getPlaceName(),
            entity.getAddress(),
            entity.getLatitude(),
            entity.getLongitude(),
            entity.getCreatedAt()
        );
    }
}
