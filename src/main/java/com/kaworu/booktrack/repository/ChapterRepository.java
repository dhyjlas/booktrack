package com.kaworu.booktrack.repository;

import com.kaworu.booktrack.entity.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findAllByBookId(long bookId);

    Page<Chapter> findAllByBookId(long bookId, Pageable pageable);

    void deleteAllByBookId(long bookId);
}
