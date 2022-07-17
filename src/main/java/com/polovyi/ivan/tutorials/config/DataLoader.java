package com.polovyi.ivan.tutorials.config;

import com.github.javafaker.CreditCardType;
import com.github.javafaker.Faker;
import com.polovyi.ivan.tutorials.entity.PurchaseTransactionEntity;
import com.polovyi.ivan.tutorials.repository.PurchaseTransactionRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Component
public record DataLoader(PurchaseTransactionRepository purchaseTransactionRepository) {

    @Bean
    private InitializingBean sendDatabase() {
        return () -> purchaseTransactionRepository.saveAll(generatePurchaseTransactionList(new Faker()));
    }

    private List<PurchaseTransactionEntity> generatePurchaseTransactionList(Faker faker) {
        List<PurchaseTransactionEntity> purchaseTransactionEntityList = IntStream.range(0, new Random().nextInt(10))
                .mapToObj(i -> PurchaseTransactionEntity.builder().createdAt(
                                LocalDate.now().minus(Period.ofDays((new Random().nextInt(365 * 10)))))
                        .amount(new BigDecimal(faker.commerce().price().replaceAll(",", ".")))
                        .paymentType(List.of(CreditCardType.values())
                                .get(new Random().nextInt(CreditCardType.values().length)).toString())
                        .customerId(UUID.randomUUID().toString())
                        .build())
                .collect(toList());
        purchaseTransactionRepository.saveAll(purchaseTransactionEntityList);
        return purchaseTransactionEntityList;
    }
}
