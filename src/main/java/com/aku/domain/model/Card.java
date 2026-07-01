package com.aku.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Card {
    private CardStatus status;
    private final BigDecimal dailyLimit;
    private final List<Payment> operationHistory;

    public Card(CardStatus status, BigDecimal dailyLimit, List<Payment> operationHistory) {
        this.status = status;
        this.dailyLimit = dailyLimit;
        this.operationHistory = new ArrayList<>(operationHistory);
    }

    public Boolean isActive () {
        return this.status.equals(CardStatus.ACTIVE);
    }

    public boolean isDailyLimitExceeded(BigDecimal amount) {
        BigDecimal spentToday = operationHistory.stream()
                .filter(p -> p.getPaymentDate().toLocalDate().equals(LocalDate.now()))
                .filter(p -> p.getPaymentStatus() == PaymentStatus.ACCEPTED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return spentToday.add(amount).compareTo(dailyLimit) > 0;
    }

    public void block() {
        this.status = CardStatus.BLOCKED;
    }

    public void unBlock() {
        this.status = CardStatus.ACTIVE;
    }

    public void addToHistory(Payment payment) {
        this.operationHistory.add(payment);
    }

    public boolean shouldBeBlocked() {
        List<Payment> history = operationHistory;
        if (history.size() < 3) return false;

        return history.subList(history.size() - 3, history.size())
                .stream()
                .allMatch(p -> p.getPaymentStatus() == PaymentStatus.REFUSED);
    }
}
