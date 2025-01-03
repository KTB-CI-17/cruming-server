package com.ci.Cruming.file.controller;

import com.ci.Cruming.file.storage.S3FileStorage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final S3FileStorage s3FileStorage;

    @GetMapping("/{fileKey}/**")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileKey, HttpServletRequest request) {
        try {
            String fullPath = request.getRequestURI().substring(request.getRequestURI().indexOf(fileKey));
            Resource resource = s3FileStorage.loadAsResource(fullPath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}