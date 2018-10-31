package com.kaworu.booktrack.repository;

import com.kaworu.booktrack.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByUrl(String url);

    Page<Book> findAll(Specification<Book> example, Pageable pageable);
}
