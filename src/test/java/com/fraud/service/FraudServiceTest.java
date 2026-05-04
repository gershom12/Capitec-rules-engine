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

        when(transactionRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Transaction tx = fraudService.saveTransaction(req);

        assertEquals("user1", tx.getUserId());
    }

    @Test
    void should_throw_when_transaction_save_fails() {
        when(transactionRepo.save(any()))
                .thenThrow(new RuntimeException());

        assertThrows(FraudProcessingException.class,
                () -> fraudService.saveTransaction(
                        new TransactionRequest("u", 1, "ZA")));
    }

    @Test
    void should_throw_when_alert_save_fails() {
        when(alertRepo.save(any()))
                .thenThrow(new RuntimeException());

        assertThrows(FraudProcessingException.class,
                () -> fraudService.saveAlert(
                        Transaction.builder().id(1L).build(),
                        List.of(new RuleResult("R", true, "msg"))
                ));
    }
}