package com.capstone.backend.model.dto.bookvolume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookVolumeDTORequest {
    @Schema(description = "BookVolume is not null", example = "Toan Hinh")
    @NotEmpty(message = "BookVolume name is not null")
    @Length(min = 4, message = "BookVolume name is greater than 3")
    String name;

    @Schema(description = "Subject id is mandatory", example = "1")
    @NotNull(message = "Subject id is mandatory")
    Long subjectId;
}
