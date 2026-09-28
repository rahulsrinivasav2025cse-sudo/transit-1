package com.example.transit.service;

import com.example.transit.entity.Journal;
import com.example.transit.exception.ResourceNotFoundException;
import com.example.transit.repository.JournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalRepository journalRepository;

    @Transactional
    public Journal createJournal(Journal journal) {
        journal.setId(null);
        return journalRepository.save(journal);
    }

    public List<Journal> getAllJournals() {
        return journalRepository.findAll();
    }

    public Journal getJournalById(Long id) {
        return journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found with id " + id));
    }
}
