package com.example.transit.controller;

import com.example.transit.entity.Journal;
import com.example.transit.service.JournalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journals")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;

    @PostMapping
    public ResponseEntity<Journal> createJournal(@Valid @RequestBody Journal journal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(journalService.createJournal(journal));
    }

    @GetMapping
    public List<Journal> getAllJournals() {
        return journalService.getAllJournals();
    }

    @GetMapping("/{id}")
    public Journal getJournal(@PathVariable Long id) {
        return journalService.getJournalById(id);
    }
}
