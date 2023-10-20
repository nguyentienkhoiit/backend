package com.capstone.backend.controller;

import com.capstone.backend.model.dto.reportresource.ReportResourceDTORequest;
import com.capstone.backend.service.ReportResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.capstone.backend.utils.Constants.API_VERSION;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION + "/report-resource")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Report resource", description = "API for Report Resource")
public class ReportResourceController {
    ReportResourceService reportResourceService;

    @PostMapping
    @Operation(summary = "Create report a resource")
    public ResponseEntity<?> createReportResource(@Valid @RequestBody ReportResourceDTORequest request) {
        return ResponseEntity.ok(reportResourceService.createReportComment(request));
    }

}
