package com.marketplace.shared.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Money {

    private BigDecimal amount;
    private String currency;

    public static Money of(BigDecimal amount, String currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        return new Money(amount, currency);
    }

    public static Money usd(BigDecimal amount) {
        return of(amount, "USD");
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money multiply(BigDecimal factor) {
        return new Money(amount.multiply(factor), currency);
    }

    private void validateSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot operate on different currencies");
        }
    }
}
