package com.capstone.backend.model.dto.subject;

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
public class SubjectDTORequest {
    @Schema(description = "Subject is not null", example = "Toan")
    @NotEmpty(message = "Subject name is not null")
    @Length(min = 4, message = "Subject name is greater than 3")
    String name;

    @Schema(description = "book seriesId is mandatory", example = "1")
    @NotNull(message = "book seriesId is mandatory")
    Long bookseriesId;
}
