package com.fraud.repository;

import com.fraud.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository repo;

    @Test
    void should_count_transactions_in_window() {
        Transaction tx = Transaction.builder()
                .userId("user1")
                .amount(100)
                .location("ZA")
                .timestamp(LocalDateTime.now())
                .build();

        repo.save(tx);

        long count = repo.countByUserIdAndTimestampAfter(
                "user1",
                LocalDateTime.now().minusMinutes(1)
        );

        assertEquals(1, count);
    }
}