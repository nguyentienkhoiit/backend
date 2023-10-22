package com.capstone.backend.controller;
import com.capstone.backend.model.dto.PagingDTOResponse;
import com.capstone.backend.model.dto.chapter.ChapterDTOFilter;
import com.capstone.backend.model.dto.chapter.ChapterDTORequest;
import com.capstone.backend.model.dto.chapter.ChapterDTOResponse;
import com.capstone.backend.model.dto.classes.ClassDTOResponse;
import com.capstone.backend.model.dto.lesson.LessonDTOFilter;
import com.capstone.backend.model.dto.lesson.LessonDTORequest;
import com.capstone.backend.model.dto.lesson.LessonDTOResponse;
import com.capstone.backend.service.LessonService;
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
@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION + "/lesson")
@Tag(name = "Lesson", description = "API for Lesson")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LessonController {
    LessonService lessonService;

    @Operation(summary = "Create Lesson")
    @PostMapping("")
    public ResponseEntity<LessonDTOResponse> create(@Valid @RequestBody LessonDTORequest request) {
        LessonDTOResponse lessonDTOResponse = lessonService.createLesson(request);
        return ResponseEntity.ok(lessonDTOResponse);
    }

    @Operation(summary = "Update Lesson")
    @PutMapping("/{id}")
    public ResponseEntity<LessonDTOResponse> update(@Valid @RequestBody LessonDTORequest request,
                                                     @PathVariable @NotEmpty Long id) {
        LessonDTOResponse lessonDTOResponse = lessonService.updateLesson(id, request);
        return ResponseEntity.ok(lessonDTOResponse);
    }

    @Operation(summary = "Delete Lesson")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable @NotEmpty Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "Search Lesson")
    @GetMapping("/display")
    public PagingDTOResponse searchLesson(@ModelAttribute LessonDTOFilter lessonDTOFilter) {
        return lessonService.searchLesson(lessonDTOFilter);
    }

    @Operation(summary = "View Lesson by Id")
    @GetMapping("/{id}")
    public ResponseEntity<LessonDTOResponse> viewLesson(@PathVariable @NotEmpty Long id) {
        LessonDTOResponse lessonDTOResponse = lessonService.viewLessonById(id);
        return ResponseEntity.ok(lessonDTOResponse);
    }
}
