package com.aku.domain.service;

import com.aku.domain.model.Account;
import com.aku.domain.model.Card;
import com.aku.domain.model.Payment;
import com.aku.domain.model.PaymentStatus;
import com.aku.domain.port.in.AuthorizePaymentUseCase;
import com.aku.domain.port.out.AccountRepository;
import com.aku.domain.port.out.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentService extends AuthorizePaymentUseCase {
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final AtomicLong paymentIdSequence = new AtomicLong(System.currentTimeMillis());

    public PaymentService(AccountRepository accountRepository, PaymentRepository paymentRepository) {
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentStatus authorizePayment(long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        Account account = accountRepository.findById(accountId);
        Card card = account.getCard();
        Payment payment = createPayment(accountId, amount);

        if (!card.isActive()) return refuse(payment, card, account);
        if (card.isDailyLimitExceeded(amount)) return refuse(payment, card, account);
        if (!account.hasSufficientBalance(amount)) return refuse(payment, card, account);

        account.debit(amount);
        payment.accept();
        card.addToHistory(payment);
        accountRepository.save(account);
        savePayment(payment);
        return PaymentStatus.ACCEPTED;
    }

    private PaymentStatus refuse(Payment payment, Card card, Account account) {
        payment.refuse();
        card.addToHistory(payment);
        savePayment(payment);
        if (card.shouldBeBlocked()) {
            card.block();
            accountRepository.save(account);
        }
        return PaymentStatus.REFUSED;
    }

    public Payment createPayment(long accountId, BigDecimal amount) {
        long id = paymentIdSequence.incrementAndGet();
        LocalDateTime currentDate = LocalDateTime.now();
        return new Payment(id, accountId, amount, currentDate);

    }

    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }
}
