package com.kaworu.booktrack.repository;

import com.kaworu.booktrack.entity.Website;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebsiteRepository extends JpaRepository<Website, Long> {

    Page<Website> findAll(Specification<Website> example, Pageable pageable);
}
