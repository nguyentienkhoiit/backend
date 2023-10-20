package com.capstone.backend.repository;

import com.capstone.backend.entity.BookSeries;
import com.capstone.backend.entity.BookVolume;
import com.capstone.backend.entity.Lesson;
import com.capstone.backend.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Page<Subject> findSubjectByNameContainsAndActiveTrue(String name, Pageable pageable);

    Optional<Subject> findByIdAndActiveTrue(Long id);

    boolean existsSubjectByBookSeriesAndActiveTrue(BookSeries bookSeries);
}
