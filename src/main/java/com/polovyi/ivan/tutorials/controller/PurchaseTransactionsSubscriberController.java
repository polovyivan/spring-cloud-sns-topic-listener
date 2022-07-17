package com.polovyi.ivan.tutorials.controller;

import com.polovyi.ivan.tutorials.service.PurchaseTransactionService;
import io.awspring.cloud.messaging.config.annotation.NotificationMessage;
import io.awspring.cloud.messaging.config.annotation.NotificationSubject;
import io.awspring.cloud.messaging.endpoint.NotificationStatus;
import io.awspring.cloud.messaging.endpoint.annotation.NotificationMessageMapping;
import io.awspring.cloud.messaging.endpoint.annotation.NotificationSubscriptionMapping;
import io.awspring.cloud.messaging.endpoint.annotation.NotificationUnsubscribeConfirmationMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("subscribers/purchase-transactions")
public class PurchaseTransactionsSubscriberController {

    private final PurchaseTransactionService purchaseTransactionService;

    @NotificationSubscriptionMapping
    public void handleSubscriptionMessage(NotificationStatus status) {

        log.info("Received subscription request!");
        status.confirmSubscription();
        log.info("Subscription confirmed!");
    }

    @NotificationUnsubscribeConfirmationMapping
    public void handleUnsubscribeMessage(NotificationStatus status) {
        log.info("Received unsubscription request!");
        status.confirmSubscription();
        log.info("Unsubscription confirmed!");
    }

    @NotificationMessageMapping
    public void handleNotificationMessage(@NotificationSubject String subject, @NotificationMessage String message) {
        purchaseTransactionService.saveTransaction(subject, message);
    }

}
