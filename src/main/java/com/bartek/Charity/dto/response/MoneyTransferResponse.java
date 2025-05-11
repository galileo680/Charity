package com.bartek.Charity.dto.response;

import com.bartek.Charity.domain.enums.Currency;

import java.math.BigDecimal;
import java.util.Map;

public record MoneyTransferResponse(
        String collectionBoxId,
        String fundraisingEventName,
        Map<Currency, BigDecimal> transferredAmounts,
        Currency targetCurrency,
        BigDecimal totalAmountInTargetCurrency
) {}