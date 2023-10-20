package com.capstone.backend.repository;

import com.capstone.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByIdAndActiveTrue(Long id);

    Optional<Tag> findByNameEqualsIgnoreCaseAndActiveTrue(String name);
}
