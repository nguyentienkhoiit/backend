package com.capstone.backend.model.dto.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResourceDTORequest {

    @Schema(example = "1", description = "Subject must be integer")
    @NotNull(message = "{subjectId.not-null}")
    Long subjectId;

    @Schema(example = "1", description = "LessonId is optional")
    Long lessonId;

    @Schema(example = "Slide is beautiful", description = "Resource name is optional")
    String name;

    @Schema(example = "description", description = "Description is optional")
    String description;

    @Schema(description = "Visual type must be choose [PUBLIC, PRIVATE, RESTRICT]", example = "PUBLIC")
    @NotBlank(message = "{visualType.not-blank}")
    @Pattern(regexp = "(PUBLIC|PRIVATE|RESTRICT)", message = "{visualType.regex-message}")
    String visualType;

    @Schema(example = "[1, 2]", description = "Tag List must be Integer array")
    @NotEmpty(message = "{tagList.not-empty}")
    String tagList;
}
