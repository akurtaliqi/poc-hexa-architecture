package domain.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Account {

    private final long id;
    private final BigDecimal availableAmount;

    public Account(long id, BigDecimal availableAmount) {
        this.id = id;
        this.availableAmount = availableAmount;
    }
}