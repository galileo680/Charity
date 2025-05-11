package com.bartek.Charity.dto.request;

import com.bartek.Charity.domain.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AddMoneyRequest(
        @NotNull(message = "Currency is required")
        Currency currency,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount
) {}