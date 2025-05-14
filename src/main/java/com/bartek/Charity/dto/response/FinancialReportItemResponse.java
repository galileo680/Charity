package com.bartek.Charity.dto.response;

import com.bartek.Charity.domain.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReportItemResponse {
    private String fundraisingEventName;
    private BigDecimal amount;
    private Currency currency;
}