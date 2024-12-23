package com.ci.Cruming.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

	private static final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

	@GetMapping("/api/v1/health")
	public Map<String, String> health() {
		Map<String, String> returnMap = new HashMap<>();
		returnMap.put("status", "ok");
		returnMap.put("path", System.getProperty("user.dir"));
		returnMap.put("os", System.getProperty("os.name").toLowerCase());

		logger.info("Health check data: {}", returnMap);
		return returnMap;
	}
}
