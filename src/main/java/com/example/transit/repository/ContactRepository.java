package com.example.transit.repository;

import com.example.transit.entity.Contact;
import com.example.transit.entity.enums.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByType(ContactType type);
}
