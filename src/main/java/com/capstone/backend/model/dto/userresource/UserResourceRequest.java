package com.capstone.backend.model.dto.userresource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResourceRequest {

    @Schema(example = "1", description = "ResourceId must be integer")
    @NotNull(message = "ResourceId is mandatory")
    Long resourceId;

    @Schema(description = "Action type must be choose [LIKE, UNLIKE, SAVED, UNSAVED]", example = "LIKE")
    @NotBlank(message = "Action type is mandatory")
    @Pattern(regexp = "(LIKE|UNLIKE|SAVED|UNSAVED)",
            message = "Action type must be choose [LIKE, UNLIKE, SAVED, UNSAVED]")
    String actionType;

}
