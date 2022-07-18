package com.polovyi.ivan.tutorials.service;

import com.polovyi.ivan.tutorials.config.JsonConverter;
import com.polovyi.ivan.tutorials.dto.PurchaseTransactionResponse;
import com.polovyi.ivan.tutorials.entity.PurchaseTransactionEntity;
import com.polovyi.ivan.tutorials.repository.PurchaseTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public record PurchaseTransactionService(PurchaseTransactionRepository purchaseTransactionRepository) {

    private static final String TRANSACTION_TYPE = "PURCHASE_TRANSACTION";

    public void saveTransaction(String subject, String message) {
        log.info("Processing sns message <<>> {} <<>>", message);

        if (!TRANSACTION_TYPE.equals(subject)) {
            log.error("Wrong message type!!! Throwing exception...");
            throw new RuntimeException();
        }

        PurchaseTransactionEntity purchaseTransactionEntity = JsonConverter.stringJsonToObject(message,
                PurchaseTransactionEntity.class);
        purchaseTransactionEntity.setCreatedAt(LocalDate.now());

        purchaseTransactionRepository.save(purchaseTransactionEntity);
        log.info("Purchase transaction saved!");
    }

    public List<PurchaseTransactionResponse> getPurchaseTransactions() {
        log.info("Returning purchase transaction...");
        return purchaseTransactionRepository.findAll().stream()
                .map(PurchaseTransactionResponse::valueOf)
                .collect(Collectors.toList());
    }
}
