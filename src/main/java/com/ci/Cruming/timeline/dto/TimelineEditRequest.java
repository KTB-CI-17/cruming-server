package com.ci.Cruming.timeline.dto;

import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.file.dto.FileRequest;
import com.ci.Cruming.location.dto.LocationRequest;

import java.time.LocalDate;
import java.util.List;

public record TimelineEditRequest(
    LocationRequest location,
    String level,
    String content,
    Visibility visibility,
    LocalDate activityAt,
    List<FileRequest> newFiles,
    List<Long> deleteFileIds
) {

}
