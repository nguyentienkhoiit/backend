package com.capstone.backend.model.dto.resourcetag;

import com.capstone.backend.entity.Resource;
import com.capstone.backend.entity.type.TableType;
import com.capstone.backend.model.dto.tag.TagDTOResponse;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ResourceTagDTOResponse {
    Long id;
    Long detailId;
    @Enumerated(EnumType.STRING)
    TableType tableType;
    TagDTOResponse tagDTOResponse;
    Resource resource;
}
