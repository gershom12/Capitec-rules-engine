package com.fraud.service;

import com.fraud.dto.RuleResult;
import com.fraud.dto.TransactionRequest;
import com.fraud.entity.Transaction;
import com.fraud.exception.FraudProcessingException;
import com.fraud.repository.FraudAlertRepository;
import com.fraud.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FraudServiceTest {

    @Mock
    private TransactionRepository transactionRepo;

    @Mock
    private FraudAlertRepository alertRepo;

    @InjectMocks
    private FraudService fraudService;

    @Test
    void should_save_transaction_successfully() {

        TransactionRequest req = new TransactionRequest("user1", 100, "ZA");

        when(transactionRepo.save(any(Transaction.class)))
                .thenAnswer(i -> {
                    Transaction t = i.getArgument(0);
                    t.setId(1L);
                    return t;
                });

        Transaction tx = fraudService.createTransaction(req);

        assertEquals("user1", tx.getUserId());
        assertEquals(100, tx.getAmount());
    }

    @Test
    void should_throw_when_transaction_save_fails() {

        when(transactionRepo.save(any()))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class,
                () -> fraudService.createTransaction(
                        new TransactionRequest("u", 1, "ZA")));
    }

    @Test
    void should_save_alert_successfully() {

        Transaction tx = Transaction.builder().id(1L).build();

        List<RuleResult> results =
                List.of(new RuleResult("R", true, "msg"));

        when(alertRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        fraudService.saveAlert(tx, results);

        verify(alertRepo, times(1)).save(any());
    }
}