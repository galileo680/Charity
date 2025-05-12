package com.bartek.Charity.dto.response;

import com.bartek.Charity.domain.enums.Currency;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record FinancialReportItemResponse(
        String fundraisingEventName,
        BigDecimal amount,
        Currency currency
) {}