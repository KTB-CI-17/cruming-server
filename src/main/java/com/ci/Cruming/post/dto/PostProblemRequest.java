package com.ci.Cruming.post.dto;

import com.ci.Cruming.file.dto.FileRequest;
import com.ci.Cruming.location.dto.LocationRequest;

public record PostProblemRequest(
        String title,
        String content,
        LocationRequest location,
        String level,
        FileRequest file
) {
}

