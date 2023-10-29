package com.capstone.backend.controller;

import com.capstone.backend.model.dto.materials.MaterialFilterProtectDTORequest;
import com.capstone.backend.model.dto.resource.ResourceDTORequest;
import com.capstone.backend.model.dto.resource.ResourceMediaDTOFilter;
import com.capstone.backend.model.dto.resource.ResourceSharedDTORequest;
import com.capstone.backend.model.dto.resource.UserShareSuggestDTORequest;
import com.capstone.backend.model.dto.tag.TagSuggestDTORequest;
import com.capstone.backend.model.mapper.MaterialsMapper;
import com.capstone.backend.service.FileService;
import com.capstone.backend.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.capstone.backend.utils.Constants.API_VERSION;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION + "/resource")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Resource", description = "API for Resource")
@CrossOrigin
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

    /**
     * APPLICATION_OCTET_STREAM_VALUE: video, mp3
     * APPLICATION_PDF_VALUE: pdf
     * IMAGE_JPEG_VALUE/IMAGE_PNG_VALUE: image
     */
//    @GetMapping("/view/{fileName}")
//    public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
//        Path imagePath = Paths.get(fileName);
//        byte[] imageData = fileService.downloadImageFromFileSystem(imagePath);
//        return ResponseEntity.status(HttpStatus.OK)
//                .contentType(MediaType.valueOf(MediaType.TEXT_HTML_VALUE))
//                .body(imageData);
//    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
        Path imagePath = Paths.get(fileName);
        byte[] imageData = fileService.downloadImageFromFileSystem(imagePath);
        MediaType mediaType = null;
        if(fileName.contains(".pdf"))
            mediaType = MediaType.APPLICATION_PDF;
        else if(fileName.contains(".png"))
            mediaType = MediaType.IMAGE_PNG;
        else if(fileName.contains(".jpeg"))
            mediaType = MediaType.IMAGE_JPEG;
        else if(fileName.contains(".mp3") || fileName.contains(".mp4"))
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(mediaType)
                .body(imageData);
    }

//    @GetMapping("/view/{fileName}")
//    public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
//        // Define the file path on the server
//        String filePath = "resource/"+fileName; // Adjust the path to the actual location of your DOCX file
//
//        Path path = Paths.get(filePath);
//        Resource resource = new UrlResource(path.toUri());
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(resource);
//    }

    //checked permission
    @GetMapping("/detail/{id}")
    @Operation(summary = "See a detail resource (comment, resource, like, unlike, resource relate ...)")
    public ResponseEntity<?> getResourceDetailById(@Valid @PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(resourceService.getResourceDetailById(id));
    }

    //checked permission
    @GetMapping("/materials")
    public ResponseEntity<?> searchMaterials(
            @Valid @ModelAttribute MaterialFilterProtectDTORequest materialFilterProtectDTORequest
    ) {
        var request = MaterialsMapper.toMaterialsFilterDTORequest(materialFilterProtectDTORequest);
        return ResponseEntity.ok(resourceService.searchMaterials(request));
    }

    @PostMapping("/tags")
    public ResponseEntity<?> getListTagsSuggest(@RequestBody TagSuggestDTORequest request) {
        return ResponseEntity.ok(resourceService.getListTagsSuggest(request));
    }

    @GetMapping("/medias")
    public ResponseEntity<?> searchMediaResource(
            @Valid @ModelAttribute ResourceMediaDTOFilter resourceDTOFilter
    ) {
        return ResponseEntity.ok(resourceService.searchMediaResource(resourceDTOFilter));
    }

    @GetMapping("/download/{filename:.+}")
    @Operation(summary = "Download a resource")
    public ResponseEntity<?> downloadResource(@PathVariable(name = "filename", required = true) String fileName) {
        Resource file = resourceService.downloadResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    //check permission resource
    @GetMapping("/share/{resourceId}")
    public ResponseEntity<?> viewResourceShareById(@PathVariable Long resourceId) {
        return ResponseEntity.ok(resourceService.viewResourceShareById(resourceId));
    }

    //check permission resource
    @PostMapping("/share/suggest")
    public ResponseEntity<?> suggestionUserShare(@RequestBody UserShareSuggestDTORequest request) {
        return ResponseEntity.ok(resourceService.suggestionUserShare(request.getText()));
    }

    //check permission resource
    @PostMapping("/share/{resourceId}")
    public ResponseEntity<?> shareResource(
            @RequestBody ResourceSharedDTORequest request,
            @PathVariable Long resourceId
    ) {
        request.setResourceId(resourceId);
        return ResponseEntity.ok(resourceService.shareResource(request));
    }

}
