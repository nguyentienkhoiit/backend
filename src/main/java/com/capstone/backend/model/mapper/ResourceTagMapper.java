package com.capstone.backend.model.mapper;

import com.capstone.backend.entity.ResourceTag;
import com.capstone.backend.model.dto.resourcetag.ResourceTagDTOResponse;
import com.capstone.backend.model.dto.tag.TagDTOResponse;

public class ResourceTagMapper {
    public static ResourceTagDTOResponse toResourceTagDTOResponse(ResourceTag resourceTag) {
        TagDTOResponse tag = TagDTOResponse.builder()
                .id(resourceTag.getTag().getId())
                .name(resourceTag.getTag().getName())
                .build();
        return ResourceTagDTOResponse.builder()
                .id(resourceTag.getId())
                .detailId(resourceTag.getDetailId())
                .tableType(resourceTag.getTableType())
                .tagDTOResponse(tag)
                .build();
    }
}
