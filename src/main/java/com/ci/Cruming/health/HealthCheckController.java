package com.ci.Cruming.health;

import com.ci.Cruming.location.dto.LocationRequest;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class HealthCheckController {

	private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);
	private final LocationService locationService;

	@GetMapping("/api/v1/health")
	public Map<String, String> health() {
		Map<String, String> returnMap = new HashMap<>();
		returnMap.put("status", "ok");
		returnMap.put("path", System.getProperty("user.dir"));
		returnMap.put("os", System.getProperty("os.name").toLowerCase());

		logger.info("Health check data: {}", returnMap);
		return returnMap;
	}

	@GetMapping("/api/v1/dbtest")
	 public ResponseEntity<Location> dbtest() {
		LocationRequest request = new LocationRequest("테스트용", "서울시 강남구 우리집", 12.23434, 23.3123124);

		return ResponseEntity.ok(locationService.getOrCreateLocation(request));
	}

}
