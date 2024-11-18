package com.ci.Cruming.location.repository;

import com.ci.Cruming.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByPlaceNameAndAddress(String placeName, String address);
}
