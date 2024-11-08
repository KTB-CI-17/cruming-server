package com.ci.Cruming.location.repository;

import com.ci.Cruming.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
