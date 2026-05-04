package com.fraud.repository;

import com.fraud.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    long countByUserIdAndTimestampAfter(String userId, LocalDateTime time);
}