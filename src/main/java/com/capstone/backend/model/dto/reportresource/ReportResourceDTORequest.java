package com.capstone.backend.model.dto.reportresource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportResourceDTORequest {
    @NotBlank(message = "{message.not-blank}")
    String message;
    @Schema(example = "1", description = "ReportId must be integer")
    @NotNull(message = "{reporterId.not-null}")
    Long reporterId;
    @Schema(example = "1", description = "ResourceId must be integer")
    @NotNull(message = "{resourceId.not-null}")
    Long resourceId;
}
