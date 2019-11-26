package com.kaworu.booktrack.repository;

import com.kaworu.booktrack.entity.Website;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebsiteRepository extends JpaRepository<Website, Long> {
}
