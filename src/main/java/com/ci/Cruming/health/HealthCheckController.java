package com.ci.Cruming.health;

import com.ci.Cruming.location.dto.LocationRequest;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
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
	private final Environment env;

	@GetMapping("/api/v1/health")
	public Map<String, String> health() {
		Map<String, String> returnMap = new HashMap<>();
		returnMap.put("status", "ok");
		returnMap.put("path", System.getProperty("user.dir"));
		returnMap.put("os", System.getProperty("os.name").toLowerCase());

		try {
			logger.info("JWT Configuration - Access Token Validity: {}, Refresh Token Validity: {}",
					env.getProperty("jwt.access-token-validity-in-seconds", "not set"),
					env.getProperty("jwt.refresh-token-validity-in-seconds", "not set"));

			logger.info("Database Configuration - URL: {}, Driver: {}, Username: {}",
					env.getProperty("spring.datasource.url", "not set"),
					env.getProperty("spring.datasource.driver-class-name", "not set"),
					env.getProperty("spring.datasource.username", "not set"));

			logger.info("JPA Configuration - Open In View: {}, Defer Init: {}, DDL Auto: {}, Show SQL: {}",
					env.getProperty("spring.jpa.open-in-view", "not set"),
					env.getProperty("spring.jpa.defer-datasource-initialization", "not set"),
					env.getProperty("spring.jpa.hibernate.ddl-auto", "not set"),
					env.getProperty("spring.jpa.show-sql", "not set"));

			logger.info("Hibernate Configuration - Format SQL: {}, Dialect: {}",
					env.getProperty("spring.jpa.properties.hibernate.format_sql", "not set"),
					env.getProperty("spring.jpa.properties.hibernate.dialect", "not set"));

			logger.info("Kakao Configuration - Client ID: {}, Redirect URI: {}",
					env.getProperty("kakao.auth.client-id", "not set"),
					env.getProperty("kakao.auth.redirect-uri", "not set"));

			logger.info("Logging Configuration - Cruming: {}, Servlet: {}, SQL Binder: {}, SQL: {}",
					env.getProperty("logging.level.com.ci.Cruming", "not set"),
					env.getProperty("logging.level.org.springframework.web.servlet", "not set"),
					env.getProperty("logging.level.org.hibernate.type.descriptor.sql.BasicBinder", "not set"),
					env.getProperty("logging.level.org.hibernate.SQL", "not set"));

			logger.info("Swagger Configuration - UI Path: {}, UI Enabled: {}, Docs Path: {}, Docs Enabled: {}",
					env.getProperty("springdoc.swagger-ui.path", "not set"),
					env.getProperty("springdoc.swagger-ui.enabled", "not set"),
					env.getProperty("springdoc.api-docs.path", "not set"),
					env.getProperty("springdoc.api-docs.enabled", "not set"));

			logger.info("File Upload Configuration - Upload Dir: {}",
					env.getProperty("file.upload-dir", "not set"));

			logger.info("AWS Configuration - Access Key: {}, Region: {}, Stack Auto: {}, S3 Bucket: {}",
					env.getProperty("cloud.aws.credentials.access-key", "not set"),
					env.getProperty("cloud.aws.region.static", "not set"),
					env.getProperty("cloud.aws.stack.auto", "not set"),
					env.getProperty("cloud.aws.s3.bucket", "not set"));

		} catch (Exception e) {
			logger.warn("Error while logging configuration: {}", e.getMessage());
		}

		logger.info("Health check data: {}", returnMap);
		return returnMap;
	}

	@GetMapping("/api/v1/dbtest")
	public ResponseEntity<Location> dbtest() {
		LocationRequest request = new LocationRequest("테스트용", "서울시 강남구 우리집", 12.23434, 23.3123124);
		return ResponseEntity.ok(locationService.getOrCreateLocation(request));
	}
}