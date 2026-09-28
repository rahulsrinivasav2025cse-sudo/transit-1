package com.example.transit.service;

import com.example.transit.entity.Contact;
import com.example.transit.exception.ResourceNotFoundException;
import com.example.transit.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    @Transactional
    public Contact createContact(Contact contact) {
        contact.setId(null);
        return contactRepository.save(contact);
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public Contact getContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id " + id));
    }

    @Transactional
    public Contact updateContact(Long id, Contact updated) {
        Contact contact = getContactById(id);
        contact.setName(updated.getName());
        contact.setType(updated.getType());
        contact.setPhone(updated.getPhone());
        contact.setEmail(updated.getEmail());
        contact.setAddress(updated.getAddress());
        return contactRepository.save(contact);
    }

    @Transactional
    public void deleteContact(Long id) {
        contactRepository.delete(getContactById(id));
    }
}
