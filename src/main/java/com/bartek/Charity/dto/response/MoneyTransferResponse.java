package com.bartek.Charity.dto.response;

import com.bartek.Charity.domain.enums.Currency;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record MoneyTransferResponse(
        String collectionBoxId,
        String fundraisingEventName,
        Map<Currency, BigDecimal> transferredAmounts,
        Currency targetCurrency,
        BigDecimal totalAmountInTargetCurrency
) {}