package com.capstone.backend.model.dto.resource;

import com.capstone.backend.entity.type.ApproveType;
import com.capstone.backend.entity.type.ResourceType;
import com.capstone.backend.entity.type.VisualType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceDTOUpdateResponse {
    Long id;
    String name;
    String description;
    ResourceType resourceType;
    LocalDateTime createdAt;
    ApproveType approveType;
    VisualType visualType;
    String thumbnailSrc;
    String resourceSrc;
    Long point;
    Long size;
    Long numberOfLike;
    Long classId;
    Long bookSeriesId;
    Long subjectId;
    Long bookVolumeId;
    Long chapterId;
    Long lessonId;
}
