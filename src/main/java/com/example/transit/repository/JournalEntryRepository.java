package com.example.transit.repository;

import com.example.transit.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findByAccount_AccountTypeAndEntryDateLessThanEqual(
            com.example.transit.entity.enums.AccountType accountType, LocalDate asOfDate);

    List<JournalEntry> findByAccount_AccountTypeAndEntryDateBetween(
            com.example.transit.entity.enums.AccountType accountType, LocalDate from, LocalDate to);

    List<JournalEntry> findByAnalyticAccount_IdAndEntryDateBetween(
            Long analyticAccountId, LocalDate from, LocalDate to);
}
