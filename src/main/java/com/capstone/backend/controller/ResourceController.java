package com.capstone.backend.controller;

import com.capstone.backend.model.dto.materials.MaterialFilterProtectDTORequest;
import com.capstone.backend.model.dto.materials.MaterialsFilterDTORequest;
import com.capstone.backend.model.dto.resource.ResourceMaterialDTOFilter;
import com.capstone.backend.model.dto.resource.ResourceDTORequest;
import com.capstone.backend.model.dto.resource.ResourceMediaDTOFilter;
import com.capstone.backend.model.mapper.MaterialsMapper;
import com.capstone.backend.service.FileService;
import com.capstone.backend.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.capstone.backend.utils.Constants.API_VERSION;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION + "/resource")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Resource", description = "API for Resource")
public class ResourceController {
    ResourceService resourceService;
    FileService fileService;

    @Operation(summary = "Upload multi resource")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadResource(
            @RequestParam("files") MultipartFile[] files,
            @ModelAttribute ResourceDTORequest request
    ) {
        return ResponseEntity.ok(resourceService.uploadResource(request, files));
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
        Path imagePath = Paths.get(fileName);
        byte[] imageData = fileService.downloadImageFromFileSystem(imagePath);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(MediaType.IMAGE_PNG_VALUE))
                .body(imageData);
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "See a detail resource (comment, resource, like, unlike, resource relate ...)")
    public ResponseEntity<?> getResourceDetailById(@Valid @PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(resourceService.getResourceDetailById(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResourceById(@Valid @PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(resourceService.getResourceById(id));
    }

    @GetMapping("/materials")
    public ResponseEntity<?> searchMaterials(
            @Valid @ModelAttribute MaterialFilterProtectDTORequest materialFilterProtectDTORequest
    ) {
        var request = MaterialsMapper
                .toMaterialsFilterDTORequest(materialFilterProtectDTORequest);
        return ResponseEntity.ok(resourceService.searchMaterials(request));
    }

    @GetMapping("/medias")
    public ResponseEntity<?> searchMediaResource(
            @Valid @ModelAttribute ResourceMediaDTOFilter resourceDTOFilter
    ) {
        return ResponseEntity.ok(resourceService.searchMediaResource(resourceDTOFilter));
    }

}
