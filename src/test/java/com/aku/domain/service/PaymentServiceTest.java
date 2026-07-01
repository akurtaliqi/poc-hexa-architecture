package com.aku.domain.service;

import com.aku.domain.model.*;
import com.aku.domain.port.out.AccountRepository;
import com.aku.domain.port.out.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void should_authorize_payment_when_account_has_sufficient_balance() {
        // Given
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("200.00"), new ArrayList<>());
        Account account = new Account(1, new BigDecimal("100.00"), card);
        when(accountRepository.findById(1)).thenReturn(account);

        // When
        PaymentStatus status = paymentService.authorizePayment(1, new BigDecimal("40.00"));

        // Then
        assertEquals(PaymentStatus.ACCEPTED, status);
        verify(accountRepository).findById(1);
        verify(accountRepository).save(account);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void should_reject_payment_when_account_has_insufficient_balance() {
        // Given
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("200.00"), new ArrayList<>());
        Account account = new Account(1, new BigDecimal("100.00"), card);
        when(accountRepository.findById(1)).thenReturn(account);

        // When
        PaymentStatus status = paymentService.authorizePayment(1, new BigDecimal("120.00"));

        // Then
        assertEquals(PaymentStatus.REFUSED, status);
        verify(accountRepository).findById(1);
        verify(accountRepository, never()).save(any());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void should_accept_payment_when_amount_equals_balance() {
        // Given
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("200.00"), new ArrayList<>());
        Account account = new Account(1, new BigDecimal("100.00"), card);
        when(accountRepository.findById(1)).thenReturn(account);

        // When
        PaymentStatus status = paymentService.authorizePayment(1, new BigDecimal("100.00"));

        // Then
        assertEquals(PaymentStatus.ACCEPTED, status);
        verify(accountRepository).save(account);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void should_throw_when_amount_is_null() {
        // When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.authorizePayment(1, null)
        );
        verifyNoInteractions(accountRepository, paymentRepository);
    }

    @Test
    void should_throw_when_amount_is_zero() {
        // When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.authorizePayment(1, BigDecimal.ZERO)
        );
        verifyNoInteractions(accountRepository, paymentRepository);
    }

    @Test
    void should_throw_when_amount_is_negative() {
        // When / Then
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.authorizePayment(1, new BigDecimal("-10.00"))
        );
        verifyNoInteractions(accountRepository, paymentRepository);
    }

    @Test
    void should_not_debit_account_when_payment_is_refused() {
        // Given
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("200.00"), new ArrayList<>());
        Account account = new Account(1, new BigDecimal("50.00"), card);
        when(accountRepository.findById(1)).thenReturn(account);

        // When
        paymentService.authorizePayment(1, new BigDecimal("200.00"));

        // Then
        assertEquals(new BigDecimal("50.00"), account.getBalance());
    }

    @Test
    void should_refuse_payment_when_card_is_blocked() {
        // Given
        Card card = new Card(CardStatus.BLOCKED, new BigDecimal("200.00"), new ArrayList<>());
        Account account = new Account(1, new BigDecimal("100.00"), card);
        when(accountRepository.findById(1)).thenReturn(account);

        // When
        PaymentStatus status = paymentService.authorizePayment(1, new BigDecimal("50.00"));

        // Then
        assertEquals(PaymentStatus.REFUSED, status);
        verify(accountRepository, never()).save(any());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void should_refuse_payment_when_daily_limit_is_exceeded() {
        // Given
        Payment previous = new Payment(1L, 1L, new BigDecimal("80.00"), LocalDateTime.now());
        previous.accept();
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("100.00"), List.of(previous));
        Account account = new Account(1, new BigDecimal("500.00"), card);
        when(accountRepository.findById(1)).thenReturn(account);

        // When
        PaymentStatus status = paymentService.authorizePayment(1, new BigDecimal("30.00"));

        // Then
        assertEquals(PaymentStatus.REFUSED, status);
        verify(accountRepository, never()).save(any());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void should_accept_payment_when_daily_limit_is_not_exceeded() {
        // Given
        Payment previous = new Payment(1L, 1L, new BigDecimal("50.00"), LocalDateTime.now());
        previous.accept();
        Card card = new Card(CardStatus.ACTIVE, new BigDecimal("100.00"), List.of(previous));
        Account account = new Account(1, new BigDecimal("500.00"), card);
        when(accountRepository.findById(1)).thenReturn(account);

        // When
        PaymentStatus status = paymentService.authorizePayment(1, new BigDecimal("50.00"));

        // Then
        assertEquals(PaymentStatus.ACCEPTED, status);
        verify(accountRepository).save(account);
        verify(paymentRepository).save(any(Payment.class));
    }
}