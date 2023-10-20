package com.capstone.backend.model.mapper;

import com.capstone.backend.entity.Tag;
import com.capstone.backend.model.dto.tag.TagDTORequest;
import com.capstone.backend.model.dto.tag.TagDTOResponse;

import java.time.LocalDateTime;

public class TagMapper {
    public static Tag toTag(TagDTORequest tagDTORequest) {
        return Tag.builder()
                .name(tagDTORequest.getName())
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    public static TagDTOResponse toTagDTOResponse(Tag tag) {
        return TagDTOResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
