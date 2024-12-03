package com.ci.Cruming.location.dto;

public record LocationRequest(
        String placeName,
        String address,
        Double latitude,
        Double longitude
) {
}
