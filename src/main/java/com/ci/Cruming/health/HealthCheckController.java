package com.ci.Cruming.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {
	
	@GetMapping("/health")
	public Map<String, String> health() {
		Map<String, String> returnMap = new HashMap<>();
		returnMap.put("status", "ok");
		returnMap.put("path", System.getProperty("user.dir"));
		returnMap.put("os", System.getProperty("os.name").toLowerCase());

		System.out.println(returnMap);
		return returnMap;
	}
}
