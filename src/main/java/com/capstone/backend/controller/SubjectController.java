package com.capstone.backend.controller;

import com.capstone.backend.model.dto.PagingDTOResponse;
import com.capstone.backend.model.dto.bookseries.BookSeriesDTOFilter;
import com.capstone.backend.model.dto.bookseries.BookSeriesDTOResponse;
import com.capstone.backend.model.dto.lesson.LessonDTOResponse;
import com.capstone.backend.model.dto.subject.SubjectDTOFilter;
import com.capstone.backend.model.dto.subject.SubjectDTORequest;
import com.capstone.backend.model.dto.subject.SubjectDTOResponse;
import com.capstone.backend.service.SubjectService;
import com.capstone.backend.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.capstone.backend.utils.Constants.API_VERSION;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION + "/subject")
@Tag(name = "Subject", description = "API for Subject")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubjectController {

    SubjectService subjectService;

    @Operation(summary = "create Subject")
    @PostMapping("")
    public ResponseEntity<SubjectDTOResponse> create(@Valid @RequestBody SubjectDTORequest request) {
        SubjectDTOResponse response = subjectService.createSubject(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update subject")
    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTOResponse> update(@Valid @RequestBody SubjectDTORequest request,
                                                        @PathVariable @NotEmpty Long id) {
        SubjectDTOResponse subjectDTOResponse = subjectService.updateSubject(id, request);
        return ResponseEntity.ok(subjectDTOResponse);
    }

    @Operation(summary = "Delete Subject")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable @NotEmpty Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "Search Subject")
    @GetMapping("/list")
    public PagingDTOResponse searchSubject(@ModelAttribute SubjectDTOFilter subjectDTOFilter) {
        return subjectService.searchSubject(subjectDTOFilter);
    }

    @Operation(summary = "View Subject by Id")
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTOResponse> viewSubject(@PathVariable @NotEmpty Long id) {
       SubjectDTOResponse subjectDTOResponse = subjectService.viewSubjectById(id);
        return ResponseEntity.ok(subjectDTOResponse);
    }

}
