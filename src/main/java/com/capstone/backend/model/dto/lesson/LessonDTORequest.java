package com.capstone.backend.model.dto.lesson;

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
public class LessonDTORequest {
    @Schema(description = "Lesson is not null", example = " Hinh Tron")
    @NotEmpty(message = "Lesson name is not null")
    @Length(min = 4, message = "Lesson name is greater than 3")
    String name;

    @Schema(description = "Chapter id is mandatory", example = "1")
    @NotNull(message = "Chapter id is mandatory")
    Long chapterId;
}
