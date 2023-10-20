package com.capstone.backend.controller;


import com.capstone.backend.model.dto.PagingDTOResponse;
import com.capstone.backend.model.dto.bookseries.BookSeriesDTOResponse;
import com.capstone.backend.model.dto.bookvolume.BookVolumeDTOFilter;
import com.capstone.backend.model.dto.bookvolume.BookVolumeDTORequest;
import com.capstone.backend.model.dto.bookvolume.BookVolumeDTOResponse;

import com.capstone.backend.model.dto.subject.SubjectDTOFilter;
import com.capstone.backend.model.dto.subject.SubjectDTORequest;
import com.capstone.backend.model.dto.subject.SubjectDTOResponse;
import com.capstone.backend.service.BookVolumeService;
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
@RequestMapping(API_VERSION + "/book-volume")
@Tag(name = "BookVolume", description = "API for BookVolume")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookVolumeController {
    BookVolumeService bookVolumeService;

    @Operation(summary = "Create BookVolume")
    @PostMapping("")
    public ResponseEntity<BookVolumeDTOResponse> create(@Valid @RequestBody BookVolumeDTORequest request) {
        BookVolumeDTOResponse bookVolumeDTOResponse = bookVolumeService.createBookVolume(request);
        return ResponseEntity.ok(bookVolumeDTOResponse);
    }

    @Operation(summary = "Update BookVolume")
    @PutMapping("/{id}")
    public ResponseEntity<BookVolumeDTOResponse> update(@Valid @RequestBody BookVolumeDTORequest request,
                                                     @PathVariable @NotEmpty Long id) {
        BookVolumeDTOResponse bookVolumeDTOResponse = bookVolumeService.updateBookVolume(id, request);
        return ResponseEntity.ok(bookVolumeDTOResponse);
    }

    @Operation(summary = "Delete BookVolume")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable @NotEmpty Long id) {
        bookVolumeService.deleteBookVolume(id);
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "Search BookVolume")
    @GetMapping("/list")
    public PagingDTOResponse searchBookVolume(@ModelAttribute BookVolumeDTOFilter bookVolumeDTOFilter) {
        return bookVolumeService.searchBookVolume(bookVolumeDTOFilter);
    }


    @Operation(summary = "View BookVolume by Id")
    @GetMapping("/{id}")
    public ResponseEntity<BookVolumeDTOResponse> viewBookVolume(@PathVariable @NotEmpty Long id) {
        BookVolumeDTOResponse bookVolumeDTOResponse = bookVolumeService.viewBookVolumeById(id);
        return ResponseEntity.ok(bookVolumeDTOResponse);
    }
}
