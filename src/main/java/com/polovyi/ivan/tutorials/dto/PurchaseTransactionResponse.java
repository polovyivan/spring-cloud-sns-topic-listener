package com.polovyi.ivan.tutorials.dto;

import com.polovyi.ivan.tutorials.entity.PurchaseTransactionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseTransactionResponse {
    private String id;

    private String paymentType;

    private BigDecimal amount;

    private String customerId;

    private LocalDate createdAt;

    public static PurchaseTransactionResponse valueOf(PurchaseTransactionEntity purchaseTransaction) {
        return builder()
                .id(purchaseTransaction.getId())
                .paymentType(purchaseTransaction.getPaymentType())
                .amount(purchaseTransaction.getAmount())
                .customerId(purchaseTransaction.getCustomerId())
                .createdAt(purchaseTransaction.getCreatedAt()).build();
    }
}
