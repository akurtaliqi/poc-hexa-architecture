package com.aku.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardTest {

    @Test
    void should_be_active_when_status_is_active() {
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("200.00"), new ArrayList<>());
        assertTrue(card.isActive());
    }

    @Test
    void should_not_be_active_when_status_is_blocked() {
        Card card = new Card(CardStatus.BLOCKED, new BigDecimal("200.00"), new ArrayList<>());
        assertFalse(card.isActive());
    }

    @Test
    void should_not_exceed_daily_limit_when_history_is_empty() {
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("100.00"), new ArrayList<>());
        assertFalse(card.isDailyLimitExceeded(new BigDecimal("50.00")));
    }

    @Test
    void should_not_exceed_daily_limit_when_total_is_below_limit() {
        Payment previous = acceptedPaymentToday(1, new BigDecimal("30.00"));
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("100.00"), List.of(previous));

        assertFalse(card.isDailyLimitExceeded(new BigDecimal("40.00")));
    }

    @Test
    void should_not_exceed_daily_limit_when_total_equals_limit() {
        Payment previous = acceptedPaymentToday(1, new BigDecimal("60.00"));
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("100.00"), List.of(previous));

        assertFalse(card.isDailyLimitExceeded(new BigDecimal("40.00")));
    }

    @Test
    void should_exceed_daily_limit_when_total_is_above_limit() {
        Payment previous = acceptedPaymentToday(1, new BigDecimal("80.00"));
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("100.00"), List.of(previous));

        assertTrue(card.isDailyLimitExceeded(new BigDecimal("30.00")));
    }

    @Test
    void should_not_count_refused_payments_towards_daily_limit() {
        Payment refused = refusedPaymentToday(1, new BigDecimal("80.00"));
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("100.00"), List.of(refused));

        assertFalse(card.isDailyLimitExceeded(new BigDecimal("90.00")));
    }

    @Test
    void should_not_count_payments_from_previous_days_towards_daily_limit() {
        Payment yesterday = acceptedPaymentYesterday(1, new BigDecimal("90.00"));
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("100.00"), List.of(yesterday));

        assertFalse(card.isDailyLimitExceeded(new BigDecimal("90.00")));
    }

    @Test
    void should_only_count_todays_accepted_payments_towards_daily_limit() {
        Payment yesterday = acceptedPaymentYesterday(1, new BigDecimal("50.00"));
        Payment todayAccepted = acceptedPaymentToday(2, new BigDecimal("50.00"));
        Payment todayRefused = refusedPaymentToday(3, new BigDecimal("50.00"));
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("100.00"),
                List.of(yesterday, todayAccepted, todayRefused));

        assertFalse(card.isDailyLimitExceeded(new BigDecimal("50.00")));
        assertTrue(card.isDailyLimitExceeded(new BigDecimal("51.00")));
    }

    private Payment acceptedPaymentToday(long id, BigDecimal amount) {
        Payment p = new Payment(id, 1L, amount, LocalDateTime.now());
        p.accept();
        return p;
    }

    private Payment refusedPaymentToday(long id, BigDecimal amount) {
        Payment p = new Payment(id, 1L, amount, LocalDateTime.now());
        p.refuse();
        return p;
    }

    private Payment acceptedPaymentYesterday(long id, BigDecimal amount) {
        Payment p = new Payment(id, 1L, amount, LocalDateTime.now().minusDays(1));
        p.accept();
        return p;
    }
}
