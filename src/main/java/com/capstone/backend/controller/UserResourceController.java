package com.capstone.backend.controller;

import com.capstone.backend.model.dto.userresource.UserResourceRequest;
import com.capstone.backend.service.UserResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.capstone.backend.utils.Constants.API_VERSION;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION + "/user-resource")
@Tag(name = "User Resource", description = "API for Action With Resource")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserResourceController {
    UserResourceService userResourceService;

    @PostMapping
    @Operation(summary = "Action with resource (LIKE, UNLIKE, SAVED, UNSAVED;)")
    public ResponseEntity<?> actionResource(@Valid @RequestBody UserResourceRequest request) {
        return ResponseEntity.ok(userResourceService.actionResource(request));
    }

    @GetMapping("/download/{filename:.+}")
    @Operation(summary = "Download a resource")
    public ResponseEntity<?> downloadResource(@PathVariable(name = "filename", required = true) String fileName) {
        Resource file = userResourceService.downloadResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
