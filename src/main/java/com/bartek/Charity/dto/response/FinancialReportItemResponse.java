package com.bartek.Charity.dto.response;

import com.bartek.Charity.domain.enums.Currency;

import java.math.BigDecimal;

public record FinancialReportItemResponse(
        String fundraisingEventName,
        BigDecimal amount,
        Currency currency
) {}