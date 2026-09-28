package com.example.transit.repository;

import com.example.transit.entity.Journal;
import com.example.transit.entity.enums.JournalType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JournalRepository extends JpaRepository<Journal, Long> {
    Optional<Journal> findFirstByType(JournalType type);
}
