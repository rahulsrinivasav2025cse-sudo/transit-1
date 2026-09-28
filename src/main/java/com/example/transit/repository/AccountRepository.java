package com.example.transit.repository;

import com.example.transit.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountCode(String accountCode);
    List<Account> findByAccountType(com.example.transit.entity.enums.AccountType accountType);
}
