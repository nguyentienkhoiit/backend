package com.capstone.backend.model.mapper;

import com.capstone.backend.entity.*;
import com.capstone.backend.entity.Class;
import com.capstone.backend.entity.type.ActionType;
import com.capstone.backend.model.dto.resource.ResourceDTOResponse;
import com.capstone.backend.model.dto.resource.ResourceDTOUpdateResponse;
import com.capstone.backend.model.dto.resource.ResourceMediaDTOCriteria;
import com.capstone.backend.model.dto.resource.ResourceViewDTOResponse;
import com.capstone.backend.repository.UserResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static com.capstone.backend.utils.Constants.HOST;
import static com.capstone.backend.utils.Constants.HOST_SERVER;

public class ResourceMapper {
    public static ResourceDTOResponse toResourceDTOResponse(Resource resource) {
        return ResourceDTOResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .description(resource.getDescription())
                .resourceType(resource.getResourceType())
                .createdAt(resource.getCreatedAt())
                .active(resource.getActive())
                .approveType(resource.getApproveType())
                .visualType(resource.getVisualType())
                .thumbnailSrc(HOST_SERVER + "/" + resource.getThumbnailSrc())
                .resourceSrc(HOST_SERVER + "/" + resource.getResourceSrc())
                .point(resource.getPoint())
                .size(resource.getSize())
                .build();
    }

    public static ResourceViewDTOResponse toResourceViewDTOResponse(Resource resource, boolean isSave) {
        return ResourceViewDTOResponse.builder()
                .id(resource.getId())
                .thumbnailSrc(HOST_SERVER + "/" + resource.getThumbnailSrc())
                .point(resource.getPoint())
                .name(resource.getName())
                .isSave(isSave)
                .build();
    }

    public static ResourceMediaDTOCriteria toResourceMediaDTOCriteria(ResourceTag resourceTag) {
        return ResourceMediaDTOCriteria.builder()
                .tableType(resourceTag.getTableType())
                .detailId(resourceTag.getDetailId())
                .build();
    }

    public static ResourceDTOUpdateResponse toResourceDTOUpdateResponse(Resource resource) {
        Lesson lesson = resource.getLesson();
        Chapter chapter = lesson.getChapter();
        BookVolume bookVolume = chapter.getBookVolume();
        Subject subject = bookVolume.getSubject();
        BookSeries bookSeries = subject.getBookSeries();
        Class classObject = bookSeries.getClassObject();


        return ResourceDTOUpdateResponse.builder()
                .classId(classObject.getId())
                .bookSeriesId(bookSeries.getId())
                .subjectId(subject.getId())
                .bookVolumeId(bookVolume.getId())
                .chapterId(chapter.getId())
                .lessonId(lesson.getId())
                .id(resource.getId())
                .name(resource.getName())
                .resourceType(resource.getResourceType())
                .resourceSrc(resource.getResourceSrc())
                .thumbnailSrc(resource.getThumbnailSrc())
                .size(resource.getSize())
                .visualType(resource.getVisualType())
                .approveType(resource.getApproveType())
                .description(resource.getDescription())
                .createdAt(resource.getCreatedAt())
                .point(resource.getPoint())
                .build();
    }
}
